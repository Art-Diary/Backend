package klieme.artdiary.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import org.springframework.stereotype.Component;

@Component
public class ImageTransfer {
	// @Value("${local.default.record.path}")
	// String RECORD_LOCAL_PATH;
	// @Value("${server.default.record.path}")
	// String RECORD_SERVER_PATH;
	// @Value("${local.default.loading.image}")
	String RECORD_LOCAL_DEFAULT_IMG = "";
	// @Value("${server.default.loading.image}")
	String RECORD_SERVER_DEFAULT_IMG = "";

	/**
	 * download image from storage
	 */
	public String downloadImage(String storagePath) throws IOException {
		String os = System.getProperty("os.name").toLowerCase();
		String imageToString;// image -> bytes -> string

		try {
			imageToString = Base64.getEncoder().encodeToString(Files.readAllBytes(
				Paths.get(storagePath)));
		} catch (Exception e) {
			String dir = os.contains("win") ? RECORD_LOCAL_DEFAULT_IMG : RECORD_SERVER_DEFAULT_IMG;
			imageToString = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(dir)));
		}
		return imageToString;
	}

	/**
	 * upload image to storage
 	 */



	// private String storePhoto() {
	// 	String os = System.getProperty("os.name").toLowerCase();
	// 	String defaultDir = os.contains("win") ? RECORD_LOCAL_PATH : RECORD_SERVER_PATH;
	// 	String bytes;
	//
	// 	try {
	// 		String fileName;
	// 		if (photo != null) {
	// 			String photoDir = defaultDir + "/" + detailPlanRecord.getDetailPlanId().getDetailPlanId();
	// 			String extension = Objects.requireNonNull(photo.getOriginalFilename())
	// 				.substring(photo.getOriginalFilename().lastIndexOf(".") + 1);
	//
	// 			fileName = photoDir + "/" + detailPlanRecord.getRecordId() + "." + extension;
	// 			bytes = Base64.getEncoder().encodeToString(IOUtils.toByteArray(photo.getInputStream()));
	// 			// 저장소에 사진 저장
	// 			deleteImages(photoDir, detailPlanRecord.getRecordId(), false);
	// 			photo.transferTo(new File(fileName));
	// 		} else {
	// 			if (Objects.equals(detailPlanRecord.getRecordPhoto(), "to be continued")) {
	// 				fileName = os.contains("win") ? RECORD_LOCAL_DEFAULT_IMG : RECORD_SERVER_DEFAULT_IMG;
	// 				bytes = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(fileName)));
	// 				fileName = RECORD_SERVER_DEFAULT_IMG;
	// 			} else {
	// 				fileName = os.contains("win") ? RECORD_LOCAL_DEFAULT_IMG : RECORD_SERVER_DEFAULT_IMG;
	// 				bytes = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(fileName)));
	// 				fileName = detailPlanRecord.getRecordPhoto();
	// 			}
	// 		}
	// 		// 경로 수정
	// 		String updateRecordTitle = recordTitle == null ? detailPlanRecord.getRecordTitle() : recordTitle;
	// 		String updateRecordBody = recordTitle == null ? detailPlanRecord.getRecordBody() : recordBody;
	// 		detailPlanRecord.updateRecord(updateRecordTitle, updateRecordBody, fileName);
	// 		detailPlanRecordRepository.save(detailPlanRecord);
	// 	} catch (Exception e) {
	// 		throw new GotBetterException(MessageType.BAD_REQUEST);
	// 	}
	// 	return bytes;
	// }
	//
	// private void deleteImages(String photoDir, Long recordId, Boolean forDelete) {
	// 	// 저장소에 사진 저장
	// 	File storeDir = new File(photoDir);
	//
	// 	if (!forDelete && !storeDir.exists()) {
	// 		try {
	// 			storeDir.mkdirs();
	// 		} catch (Exception e) {
	// 			e.getStackTrace();
	// 		}
	// 	} else {
	// 		String[] files = storeDir.list();
	//
	// 		if (files == null) {
	// 			return;
	// 		}
	// 		for (String file : files) {
	// 			if (file.startsWith(recordId + ".")) {
	// 				File image = new File(photoDir + "/" + file);
	// 				image.delete();
	// 				if (forDelete && files.length == 1) {
	// 					File dir = new File(photoDir);
	// 					dir.delete();
	// 				}
	// 			}
	// 		}
	//
	// 	}
	// }
}
