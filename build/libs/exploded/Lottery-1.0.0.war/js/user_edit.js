$(document).ready(function() {
//	if(userUnLogin()) {
//		return;
//	}

	var userId = getUrlParam("userId");
	getUserInfo();

	layui.use('form', function() {
		var form = layui.form;
		form.on('submit(add)', function(data) {
			var jsonData = getFormJsonData("form_data");
			var pwd = jsonData.pwd;
			var rePwd = jsonData.rePwd;
			if(pwd != rePwd){
				showToast("两次密码不一致")
				return;
			}
			var status = jsonData.status;
			if(status == undefined){
				jsonData.status = "1";
			}else{
				jsonData.status = "0";
			}
			jsonData.userId = userId;
			
			updateUserInfo(jsonData);
			
			return false;
		});
	});

	function updateUserInfo(jsonData) {
		postBody(getHost() + "sys/user/updateUserInfo?userId=" + userId, 
			jsonData, function success(res) {
				showToast(res.msg)
				console.log(res)
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
					
					$("#input_idCard").val(res.data.idCard)
					$("#input_address").val(res.data.address)
					
					$("#input_left_money").val(fomcatMoney(res.data.leftMoney))
					$("#input_cold_money").val(fomcatMoney(res.data.coldMoney))
					$("#input_bank_name").val(res.data.bankName)
					$("#input_bank_card").val(res.data.bankCard)
					
					
					
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
	
	function fomcatMoney(b){
		if(b == null || b.length < 1 || b == '0' || b == '0.0' || b == '0.00'){
			return '';
		}
		return b;
	}
})