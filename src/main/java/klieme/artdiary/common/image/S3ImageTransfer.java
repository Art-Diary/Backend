package klieme.artdiary.common.image;

import static klieme.artdiary.common.SecurityUtil.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import lombok.Builder;
import lombok.Getter;

@Component
public class S3ImageTransfer {

	private final AmazonS3Client amazonS3Client;
	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	public S3ImageTransfer(AmazonS3Client amazonS3Client) {
		this.amazonS3Client = amazonS3Client;
	}

	@Getter
	@Builder
	public static class UploadQuery {
		private final ImageType type;
		private final MultipartFile image;
		// for thumbnail
		private final Long diaryId;
		// for reg exh
		private final Long regExhId;
		// for exh
		private final Long exhId;
		// for update image
		private final String prevImagePath;
	}

	/**
	 * upload image to storage
	 * /profile/{userId}.png
	 * /thumbnail/{diaryId}.png
	 */
	public String uploadImageToStorage(UploadQuery query) {
		MultipartFile file = query.getImage();

		if (file == null) {
			return null;
		}
		String fileName;

		// 타입 별 저장할 위치 결정
		if (query.getType() == ImageType.PROFILE) {
			fileName = "profile/" + getUserId() + "_";
		} else if (query.getType() == ImageType.THUMBNAIL) {
			fileName = "thumbnail/" + query.getDiaryId() + "_";
		} else if (query.getType() == ImageType.REG_EXH) {
			fileName = "reg_exh/" + query.getRegExhId() + "_";
		} else {
			fileName = "exh/" + query.getExhId() + "_";
		}
		// 업데이트 할 때 이전 사진 삭제
		if (query.getPrevImagePath() != null) {
			deleteFile(query.getPrevImagePath(), fileName);
		}
		fileName += file.getOriginalFilename();
		return updateFile(file, fileName);
	}

	@Getter
	@Builder
	public static class UploadContentImagesQuery {
		private final MultipartFile[] images;
		private final Long diaryId;
		private final String contents;
	}

	public String uploadContentImagesToStorage(UploadContentImagesQuery query) {
		MultipartFile[] images = query.getImages();

		if (images == null || images.length == 0) {
			return query.getContents();
		}
		String fileDir = "diary/" + query.getDiaryId() + "/";

		// 모두 지우기
		deleteFolder(fileDir);
		// store images
		String contents = query.getContents();
		for (MultipartFile image : images) {
			String fileName = fileDir + image.getOriginalFilename();

			String s3ImageUrl = updateFile(image, fileName);

			if (s3ImageUrl == null) {
				continue;
			}
			contents = contents.replaceAll(Objects.requireNonNull(image.getOriginalFilename()), s3ImageUrl);
		}
		return contents;

	}

	private String updateFile(MultipartFile file, String fileName) {
		try {
			ObjectMetadata metadata = new ObjectMetadata();

			metadata.setContentType(file.getContentType());
			metadata.setContentLength(file.getSize());
			amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);

			return amazonS3Client.getUrl(bucket, fileName).toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void deleteFolder(String fileDir) {
		ListObjectsV2Result result = amazonS3Client.listObjectsV2(bucket, fileDir);
		List<S3ObjectSummary> objects = result.getObjectSummaries();

		for (S3ObjectSummary os : objects) {
			System.out.println(os.getKey());
			String key = os.getKey();

			amazonS3Client.deleteObject(bucket, key);
		}
	}

	private void deleteFile(String fileName, String target) {
		URL url = amazonS3Client.getUrl(bucket, fileName.substring(fileName.indexOf(target)));
		String key = url.getPath().substring(1);

		// 파일 존재 여부 확인
		if (amazonS3Client.doesObjectExist(bucket, key)) {
			// S3에서 파일 삭제
			amazonS3Client.deleteObject(bucket, key);
			System.out.println("File deleted successfully: " + key);
		} else { // file not found
			System.out.println("File not found: " + key);
		}
	}

	private Long getUserId() {
		return getCurrentUserEntity().getUserId();
	}
}
