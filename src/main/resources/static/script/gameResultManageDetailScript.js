window.onload = function() {
	// レート再計算が発生する旨を表示
	var occurRestoreButton = document.getElementById("occur_restore_button");
	occurRestoreButton.addEventListener("click", function() {
	  alert('更新した対局結果以降の対局について、レート計算は初期化されます。');
	});
};