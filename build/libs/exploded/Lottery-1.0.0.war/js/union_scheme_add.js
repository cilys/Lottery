$(document).ready(function(){
	
	layui.use('form', function() {
		var form = layui.form;
		form.on('submit(add)', function(data) {
			var jsonData = getFormJsonData("form_data");
			log(jsonData)
			
			var status = jsonData.status;
			if(status == undefined){
				jsonData.status = "1";
			}else{
				jsonData.status = "0";
			}
			
			var totalMoney = jsonData["totalMoney"];
			var reg = /(^[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)/;
        	if (reg.test(totalMoney)) {
        		
        	} else{
        		showToast("请输入正确的金额,且最多两位小数!")
        		return false;
        	}
			
			add(jsonData);
			
			return false;
		});
	});
	
	layui.use("laydate", function(){
		var laydate = layui.laydate;
		laydate.render({
			elem : '#input_out_of_time',
			type : 'datetime'
		});
	});
	
	
	
	function add(jsonData){
		postBody(getHost() + "sys/scheme/add", 
		jsonData
		, function success(res){
			showToast(res.msg)
			log(res)
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