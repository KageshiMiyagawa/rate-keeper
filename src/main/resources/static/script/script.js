window.onload = function() {
	// 検索フォームを初期表示 その他のフォームは非表示
	document.getElementById("search_form").style.display ="block";
	document.getElementById("regist_form").style.display ="none";
	document.getElementById("upload_form").style.display ="none";
	document.getElementById("regist_info_form").style.display ="none";
	dispSelectedForm();
	dispTableSetting();
};

function dispForm(formType) {
	dispElemName = "";
	elemNameArray = ["search_form", "regist_form", "upload_form", "regist_info_form"];
	if (formType == "search") {
		dispElemName = "search_form";
	} else if (formType == "regist") {
		dispElemName = "regist_form";
	} else {
		dispElemName = "upload_form";
	}	
	dispElem = document.getElementById(dispElemName);
	dispElem.style.display = "block";
	// 表示対象以外のフォームは非表示
	for (var i = 0; i < elemNameArray.length; i++) {
		elemName = elemNameArray[i];
		if (dispElemName != elemName) {
			hideElem = document.getElementById(elemName);
			hideElem.style.display = "none";
		}
	}
}

function dispTableSetting() {
	var tableList = document.getElementsByClassName("base_table");
	for(let i = 0; i <= tableList.length; i++) {
		var table = tableList[i];
		if (table.rows.length == 1) {
			// ヘッダ行のみの場合は非表示
			table.style.display = "none";
		}
	}
}

function dispSelectedForm() {
	var selectedFormField = document.getElementById("selectedFormField");
	var selectedFormName = selectedFormField.getAttribute("selectedFormName");
	if (selectedFormName == "regist_form") {
		document.getElementById("search_form").style.display ="none";
		document.getElementById("regist_form").style.display ="block";
		var dispInfoFormField = document.getElementById("dispInfoFormField");
		var dispInfoFormFlg = dispInfoFormField.getAttribute("dispInfoFormFlg");
		if (dispInfoFormFlg == 1) {
			document.getElementById("regist_info_form").style.display ="block";		
		}
		
	} else if (selectedFormName == "upload_form") {
		document.getElementById("search_form").style.display ="none";
		document.getElementById("upload_form").style.display ="block";		
	} else {
		// search_formは表示済のため処理不要
	}
	console.log(selectedFormName);
}