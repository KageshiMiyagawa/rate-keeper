package jp.co.ratekeeper.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;

import jp.co.ratekeeper.ApplicationConstants;
import jp.co.ratekeeper.code.Grade;
import jp.co.ratekeeper.code.UserType;
import jp.co.ratekeeper.model.UserData;
import jp.co.ratekeeper.model.UserFetchCond;
import jp.co.ratekeeper.model.table.TtUser;
import jp.co.ratekeeper.repository.UserManageRepository;
import jp.co.ratekeeper.util.DateTimeUtil;

@Service
public class UserManageService {

	@Autowired
	private UserManageRepository userManageRepository;

	public List<UserData> find(UserFetchCond userFetchCond) throws Exception {
		List<TtUser> userList = userManageRepository.findUserByFetchCond(userFetchCond);
		return userList.stream().map(o -> convertUserDataForDisp(o)).collect(Collectors.toList());
	}

	public List<UserData> findUser(List<Integer> userId) {
		List<TtUser> userList = new ArrayList<>();
		if (CollectionUtils.isEmpty(userId)) {
			userList = userManageRepository.findAll();
		} else {
			userList = userManageRepository.findAllByIds(userId);
		}
		return userList.stream().map(o -> convertUserDataForDisp(o)).collect(Collectors.toList());
	}
	
	@Transactional
	public UserData save(UserData userData) {
		TtUser user = new TtUser();
		BeanUtils.copyProperties(userData, user);
		user.setUpdateDate(DateTimeUtil.getNowDateStr(ApplicationConstants.DATETIME_FORMAT));
		
		if (Objects.isNull(user.getRate())) {
			user.setRate(ApplicationConstants.DEFAULT_RATE);
		}
		return convertUserDataForDisp(userManageRepository.save(user));
	}
	
	@Transactional
	public List<TtUser> saveCsvFileData(MultipartFile file) throws Exception {
		List<TtUser> userList = new ArrayList<>();
		String nowDate = DateTimeUtil.getNowDateStr(ApplicationConstants.DATE_FORMAT);
		try (CSVReader csvReader = new CSVReader(new BufferedReader(new InputStreamReader(file.getInputStream())))) {
			List<String[]> lines = csvReader.readAll();
			
			// Header行は読込対象外
			lines.remove(0);
			for (String[] line : lines) {
				TtUser ttUser = new TtUser();
				ttUser.setUserType(line[0]);
				ttUser.setUserName(line[1]);
				ttUser.setGrade(line[2]);
				ttUser.setRate(Integer.parseInt(line[3]));
				ttUser.setJoinDate(line[4]);
				ttUser.setUpdateDate(nowDate);
				userList.add(ttUser);
			}
			// Header行は読込対象外
			//	userList.remove(0);
		}
		return userManageRepository.saveAll(userList);
	}
	
	private UserData convertUserDataForDisp (TtUser user) {
		UserData userData = new UserData();
		BeanUtils.copyProperties(user, userData);
		userData.setUserType(
				UserType.getUserTypeByCode(user.getUserType()).getName());
		userData.setGrade(Grade.getGradeByCode(user.getGrade()).getName());
		return userData;
	}

}
