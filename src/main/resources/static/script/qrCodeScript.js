window.onload = function() {
	// 検索フォームを初期表示 その他のフォームは非表示
	document.getElementById("qrCodeField").style.display ="none";
	
	var hiddenField = document.getElementById("hiddenField");
	var selectedUserValue = hiddenField.getAttribute("selectedUserValue");
	if (selectedUserValue != null) {
		document.getElementById("qrCodeField").style.display ="block";
	}
	
	dispTableSetting();
};