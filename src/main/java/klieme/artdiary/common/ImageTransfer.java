package klieme.artdiary.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.Getter;

@Component
public class ImageTransfer {
	// @Value("${local.default.record.path}")
	String RECORD_LOCAL_PATH = "C:/Users/Chaerin/Desktop/PROJECTS/artdiary/images";
	// @Value("${server.default.record.path}")
	String RECORD_SERVER_PATH;
	// @Value("${local.default.loading.image}")
	String RECORD_LOCAL_DEFAULT_IMG = "C:/Users/Chaerin/Desktop/PROJECTS/artdiary/images/default.png";
	// @Value("${server.default.loading.image}")
	String RECORD_SERVER_DEFAULT_IMG = "";

	/**
	 * download image from storage
	 */
	public String downloadImage(String storagePath) throws IOException {
		String os = System.getProperty("os.name").toLowerCase();
		String imageToString;// image -> bytes -> string

		try {
			// image 정보를 string 형으로 전환
			imageToString = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(storagePath)));
		} catch (Exception e) {
			String defaultDir = os.contains("win") ? RECORD_LOCAL_DEFAULT_IMG : RECORD_SERVER_DEFAULT_IMG;
			imageToString = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(defaultDir)));
		}
		return imageToString;
	}

	/**
	 * upload image to storage
	 * userId/thumbnail/solo/{soloDiaryId}.png
	 * userId/thumbnail/gathering/{gatherId}/{gatherDiaryId}.png
	 * userId/profile/{userId}.png
	 */
	@Getter
	@Builder
	public static class UploadQuery {
		private final ImageType type;
		private final MultipartFile image;
		private final Long soloDiaryId;
		private final Long gatherId;
		private final Long gatherDiaryId;
	}

	@Getter
	@Builder
	public static class FindUploadResult {
		private final String imageToString;
		private final String storedPath;
	}

	public FindUploadResult uploadImage(UploadQuery query) throws IOException {
		String os = System.getProperty("os.name").toLowerCase();
		String defaultDir = os.contains("win") ? RECORD_LOCAL_PATH : RECORD_SERVER_PATH;
		String imageToString;
		MultipartFile imageFile = query.getImage() != null && query.getImage().isEmpty() ? null : query.getImage();

		// 타입 별 저장할 위치 결정
		if (query.getType() == ImageType.PROFILE) {
			defaultDir += ("/profile/" + getUserId());
		} else if (query.getType() == ImageType.THUMBNAIL_SOLO) {
			defaultDir += ("/thumbnail/solo/" + query.getSoloDiaryId());
		} else if (query.getType() == ImageType.THUMBNAIL_GATHER) {
			defaultDir += ("/thumbnail/gathering/" + query.getGatherId() + "/" + query.getGatherDiaryId());
		}
		// 이미지 저장 및 string 형으로 전환
		try {
			// 확장자
			assert imageFile != null;
			String extension = Objects.requireNonNull(imageFile.getOriginalFilename())
				.substring(imageFile.getOriginalFilename().lastIndexOf(".") + 1);
			defaultDir += ("." + extension);
			// image 정보를 string 형으로 전환
			imageToString = Base64.getEncoder().encodeToString(IOUtils.toByteArray(imageFile.getInputStream()));
			// 새 폴더 생성 및 기존 파일 삭제
			if (!checkDirAndFiles(defaultDir, imageToString)) {
				// 저장소에 저장
				imageFile.transferTo(new File(defaultDir));
			}
		} catch (Exception e) {
			// // 요청 image가 null인 경우 디비에 저장된 기존 사진이 존재하면 반환하고 아니면 기본 사진을 반환
			// imageToString = checkFiles(defaultDir);
			// System.out.println("3. ]]]]]]]]]]]]]]]]]]]]]" + defaultDir + "]]]]]]]]]]]]]]]]]]]]]");
			//
			// System.out.println("4. ]]]]]]]]]]]]]]]]]]]]]" + imageToString + "]]]]]]]]]]]]]]]]]]]]]");
			// if (imageToString == null) {
			// 	imageToString = Base64.getEncoder()
			// 		.encodeToString(Files.readAllBytes(
			// 			Paths.get(os.contains("win") ? RECORD_LOCAL_DEFAULT_IMG : RECORD_SERVER_DEFAULT_IMG)));
			// }
			// defaultDir = null;
			throw new ArtDiaryException(MessageType.BAD_REQUEST);
		}
		return FindUploadResult.builder()
			.imageToString(imageToString)
			.storedPath(defaultDir)
			.build();
	}

	private boolean checkDirAndFiles(String imageDir, String newImageToString) {
		String dir = imageDir.substring(0, imageDir.lastIndexOf("/"));
		String imageName = imageDir.substring(imageDir.lastIndexOf("/") + 1, imageDir.lastIndexOf("."));
		File storeDir = new File(dir);
		boolean check = false;

		if (!storeDir.exists()) {
			// 사진 저장 경로가 없는 경우
			boolean mkdir = storeDir.mkdirs();
			if (!mkdir) {
				throw new ArtDiaryException(MessageType.BAD_REQUEST);
			}
		} else {
			// 이미 이름이 같은 사진이 있는 경우
			String[] images = storeDir.list();

			if (images != null) {
				for (String image : images) {
					if (image.startsWith(imageName + ".")) {
						try {
							// 이미 저장된 사진인 경우 확인
							String oldImageToString = Base64.getEncoder()
								.encodeToString(Files.readAllBytes(Paths.get(dir + "/" + image)));

							if (Objects.equals(oldImageToString, newImageToString)) {
								check = true;
								continue;
							}
						} catch (Exception e) {
							throw new ArtDiaryException(MessageType.BAD_REQUEST);
						}
						// 업로드 하려는 사진과 기존 사진이 다르면 기존 사진 삭제
						File oldFile = new File(dir + "/" + image);
						boolean delete = oldFile.delete();
						if (!delete) {
							throw new ArtDiaryException(MessageType.BAD_REQUEST);
						}
					}
				}
			}
		}
		return check;
	}

	private Long getUserId() {
		return UserIdFilter.getUserId();
	}
}
