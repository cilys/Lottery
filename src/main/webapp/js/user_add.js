$(document).ready(function(){
	
	layui.use('form', function() {
		var form = layui.form;
		form.on('submit(add)', function(data) {
			alert(getFormJsonStringData("form_data"))
			return false;
		});
	});
	
	$("#btn_add0").on("click", function(){
		
		var flag = true;
		if(flag){
			return false;
		}
		
		
		var userName = $("#input_user_name").val()
		if(strIsEmpty(userName)){
			showToast("请输入登录账号")
			return;
		}
		var realName = $("#input_real_name").val()
		var status = $("#cb_status").prop("checked")
		var pwd = $("#input_pwd").val()
		if(strIsEmpty(pwd)){
			showToast("请输入密码")
			return;
		}
		var rePwd = $("#input_re_pwd").val()
		if(strIsEmpty(rePwd)){
			showToast("请重复密码")
			return;
		}
		if(pwd != rePwd){
			showToast("两次密码不一致")
			return;
		}
		var sex = $("input[name='sex']:checked").val()
		var phone = $("#input_phone").val()
		var idCard = $("#input_idcard").val()
		var address = $("#input_address").val()
		var userIdentify = $("input[name='userIdentify']:checked").val()
		
		addUser(userName, pwd, realName, sex, phone, address, idCard, status ? "0" : "1", userIdentify)
	})
	
	function addUser(userName, pwd, realName, sex, phone, address, idCard, status, userIdentify){
		postBody(getHost() + "sys/user/addUser", 
		{
			userName : userName
			, pwd : pwd
			, realName : realName
			, sex : sex
			, phone : phone
			, address : address
			, idCard : idCard
			, status : status
			, userIdentify : userIdentify
		}, function success(res){
			showToast(res.msg)
			console.log(res.data)
			if(res.code == 0){
				dismissDialog();
				reloadParent()
			}
		}, function error(err){
			showToast("添加用户失败，请重试..")
		}
		)
	}
})