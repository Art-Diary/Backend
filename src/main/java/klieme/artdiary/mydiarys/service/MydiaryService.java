package klieme.artdiary.mydiarys.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import klieme.artdiary.exhibitions.data_access.repository.ExhRepository;
import klieme.artdiary.exhibitions.data_access.repository.UserExhRepository;
import klieme.artdiary.mydiarys.data_access.repository.MydiaryRepository;

@Service
public class MydiaryService implements MydiaryOperationUseCase, MydiaryReadUseCase {

	private final MydiaryRepository mydiaryRepository;

	@Autowired
	public MydiaryService(MydiaryRepository mydiaryRepository) {
		this.mydiaryRepository = mydiaryRepository;
	}

}
