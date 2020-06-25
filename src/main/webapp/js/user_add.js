$(document).ready(function(){
	
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
			
			addUser(jsonData);
			
			return false;
		});
	});
	
	function addUser(jsonData){
		postBody(getHost() + "sys/user/addUser",  jsonData,
			function success(res){
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