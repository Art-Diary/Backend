package klieme.artdiary.common.image;

import static klieme.artdiary.common.SecurityUtil.*;

import java.io.IOException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;

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
		// for update image
		private final String prevImagePath;
	}

	/**
	 * upload image to storage
	 * /profile/{userId}.png
	 * /thumbnail/{diaryId}.png
	 */
	public String uploadImageToStorage(UploadQuery query) {
		try {
			MultipartFile file = query.getImage();

			if (file == null) {
				return null;
			}
			String fileName;
			ObjectMetadata metadata = new ObjectMetadata();

			// 타입 별 저장할 위치 결정
			if (query.getType() == ImageType.PROFILE) {
				fileName = "profile/" + getUserId() + "_";
			} else {
				fileName = "thumbnail/" + query.getDiaryId() + "_";
			}
			// 업데이트 할 때 이전 사진 삭제
			if (query.getPrevImagePath() != null) {
				checkSameName(query.getPrevImagePath(), fileName);
			}
			fileName += file.getOriginalFilename();
			metadata.setContentType(file.getContentType());
			metadata.setContentLength(file.getSize());
			amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);
			return amazonS3Client.getUrl(bucket, fileName).toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void checkSameName(String fileName, String target) {
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
		return getCurrentUserId();
	}
}
