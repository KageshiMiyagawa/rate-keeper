window.onload = function() {
	var optionalMethodField = document.getElementById("optionalMethodField");
	var optionalMethod = optionalMethodField.getAttribute("optionalMethod");
	if (optionalMethod == null) {
		// 画面表示からQRコードリーダーで読み取り待機モード
		var userId = document.getElementById("userId");
		userId.focus();
		userId.addEventListener("input", function() {
			var scanValue = userId.value;
			if (isUrlFormat(scanValue)) {
				// URL形式(QRから読み取り)した場合、ユーザーIDのみ取得して設定
				var scanValueSplit = scanValue.split("/");
				scanValue = scanValueSplit[scanValueSplit.length - 1];
				userId.value = scanValue;
			}
			if (scanValue == null || scanValue == 0) {
				return;
			}
			scanGameResult(scanValue);
		});
		return;
	} else {
		// AjaxにてPOST実行モード
		var optionalParamField = document.getElementById("optionalParamField");
		var optionalValue = optionalParamField.getAttribute("optionalValue");
		scanGameResult(optionalValue);
	}
};

function scanGameResult(userId) {
	var userIdForm = document.getElementById("userId");
	userIdForm.value = "";
	var message = document.getElementById("message");
	$.ajax({
	  url: '/scan/game-result/' + userId,
	  type: "POST",
	  dataType: "json",
	  success: function(data) {
		message.innerHTML = "スキャンしたユーザーの対局結果を登録しました。ユーザID:" + userId;
	    dispAjaxResult(data);
	  },
	  error: function(jqXHR, textStatus, errorThrown) {
		  message.innerHTML = "";
		  $("#scan_result").empty();
		  $("#warnMessage").empty();
		  if (jqXHR.status === 400) {
			  var errorMessage = document.getElementById("errorMessage");
			  errorMessage.innerHTML = jqXHR.responseText;
		  } else {
			  console.log(textStatus, errorThrown);
		  }
	  }
	});
}

function dispAjaxResult(data) {
	var warnMessage = document.getElementById("warnMessage");
	var loser = data.loser;
	if(loser == null) {
		loser = "未設定（スキャン待ち）";
		warnMessage.innerHTML = "次にスキャンしたユーザーが敗者に設定されます。";
	} else {
		warnMessage.innerHTML = "";
	}
	
	var $disp = $("#scan_result");
	$disp.empty();
	var dispHtml = "<div id='delete_form' class='form'>"
		+ "<h3>対局結果読み取り情報</h3>"
		+ "<p>対局ID：" + data.gameId + "</p>"
		+ "<p>勝者：" + data.winner + "</p>"
		+ "<p>敗者：" + loser + "</p>"
		+ "<p>対局日時：" + data.gameDate + "</p>"
/** DELETEメソッドが動作しないため、コメントアウト。
		+ "<form method='post' th:action='@{/scan/game-result/delete/"+data.gameId+"}'>"
		+ "<input type='hidden' name='_method' value='DELETE'"
		+ "<div class='form_button'><button type='submit'>読み取り取消</button></div>"
		+ "</form>"
*/
		+ "</div>"
	$disp.append(dispHtml);
	var errorMessage = document.getElementById("errorMessage");
	errorMessage.innerHTML = "";
}

function isUrlFormat(str) {
  try {
    new URL(str);
    return true;
  } catch (err) {
    return false;
  }
}