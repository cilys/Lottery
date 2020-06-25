$(document).ready(function() {
//	if(userUnLogin()) {
//		return;
//	}

	var userId = getUrlParam("userId");
	getUserInfo();

	$("#btn_add").on("click", function() {
		var userName = $("#input_user_name").val()
		if(strIsEmpty(userName)) {
			showToast("请输入登录账号")
			return;
		}
		var realName = $("#input_real_name").val()
		var status = $("#cb_status").prop("checked")
		var pwd = $("#input_pwd").val()
		if(strIsEmpty(pwd)) {
			showToast("请输入密码")
			return;
		}
		var rePwd = $("#input_re_pwd").val()
		if(strIsEmpty(rePwd)) {
			showToast("请重复密码")
			return;
		}
		if(pwd != rePwd) {
			showToast("两次密码不一致")
			return;
		}
		var sex = $("input[name='sex']:checked").val()
		var phone = $("#input_phone").val()
		var idCard = $("#input_idcard").val()
		var address = $("#input_address").val()

		updateUserInfo(userName, pwd, realName, sex, phone, address, idCard, status ? "0" : "1")
	})

	function updateUserInfo(userName, pwd, realName, sex, phone, address, idCard, status) {
		postBody(getHost() + "sys/user/updateUserInfo", {
			userName: userName,
			pwd: pwd,
			realName: realName,
//			sex: sex,
			phone: phone,
			address: address,
			idCard: idCard,
			status: status,
			userId : userId
		}, function success(res) {
			showToast(res.msg)
			console.log(res.data)
			if(res.code == 0) {
				dismissDialog();
				reloadParent()
			}
		}, function error(err) {
			showToast("更新用户信息失败，请重试..")
		})
	}

	function getUserInfo() {
		post(getHost() + "user/userInfo?userId=" + userId, {
				userId: userId
			},
			function success(res) {
				showToast(res.msg);
				if(res.code == 0) {
					$("#input_user_name").val(res.data.userName)
					$("#input_real_name").val(res.data.realName)
					var status = res.data.status;
					$("#cb_status").prop("checked", status == 0 ? true : false)
					$("#input_pwd").val(res.data.pwd)
					$("#input_re_pwd").val(res.data.pwd)
					var sex = res.data.sex
					$("input[name='sex'][value = '" + sex + "']").attr("checked", true)
					
					$("#input_phone").val(res.data.phone)
					
					var userIdentify = res.data.userIdentify;
					
					$("input[name='userIdentify'][value = '" + userIdentify + "']").attr("checked", true)
				
					layui.use("form", function(){
						var form = layui.form;
						form.render()
					})
				}
			},
			function error(err) {
				showToast("获取用户信息失败，请重试..")
			}
		)
	}
})