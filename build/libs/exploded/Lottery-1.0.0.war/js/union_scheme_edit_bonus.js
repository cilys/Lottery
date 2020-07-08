$(document).ready(function(){
	var id = getUrlParam("id");
	
	queryById();
	
	layui.use('form', function() {
		var form = layui.form;
		form.on('submit(add)', function(data) {
			var jsonData = getFormJsonData("form_data");
			
			var totalBonus = jsonData["totalBonus"];
        	if (checkMoney(totalBonus)) {
        		
        	} else{
        		showToast("请输入正确的金额，最多两位小数!")
        		return false;
        	}
        	var bonusRate = jsonData.bonusRate;
        	if(checkMoney(bonusRate)){
        		var rate = Number(bonusRate);
        		if(rate >= 0 || rate <= 100){
        			
        		}else{
        			showToast("请输入正确的奖金所需缴纳的税率，0~100之间，最多两位小数!")
        			return false;
        		}
        	}else{
        		showToast("请输入正确的奖金所需缴纳的税率，0~100之间，最多两位小数!")
        		return false;
        	}
        	var canUseBouns = jsonData.canUseBonus;
        	if(canUseBouns != null && canUseBouns.length > 0){
        		if(checkMoney(canUseBouns)){
        			
        		} else {
        			showToast("请输入正确的可用奖金金额，最多两位小数!")
        			return false;
        		}
        	}
			
			jsonData.id = id;
			
			log("更新奖金参数：" + jsonData)
			updateBonus(jsonData);
			
			return false;
		});
	});
	
	function updateBonus(jsonData){
		postBody(getHost() + "sys/scheme/updateBonus?id=" + id, 
		jsonData
		, function success(res){
			showToast(res.msg)
			log(res)
			if(res.code == 0){
				dismissDialog();
				reloadParent()
			}
		}, function error(err){
			showToast("更新失败，请重试..")
		}
		)
	}
	
	function queryById(){
		post(getHost() + "sys/scheme/queryById", {
			id : id
		}, function success(res){
			showToast(res.msg)
			if(res.code == 0){
				$("#input_name").val(res.data.name);				
				$("#input_total_money").val(res.data.totalMoney);				
				$("#input_out_of_time").val(res.data.outOfTime);
				if(res.data.status == 0){
					$("#cb_status").prop("checked", true);
				}else{
					$("#cb_status").removeAttr("checked");
				}
				
				$("#input_total_bonus").val(fomcatBonus(res.data.totalBonus));
				$("#input_bonus_rate").val(fomcatBonus(res.data.bonusRate));
				$("#input_can_use_bonus").val(fomcatBonus(res.data.canUseBonus));
				
				$("#input_selled_money").val(res.data.selledMoney);				
				$("#input_payed_money").val(res.data.payedMoney);
				
				
				$("#textarea_descption").html(res.data.descption);
				
				layui.use('form', function() {
					var form = layui.form;
					form.render("checkbox");
				});
			}
		}, function error(err){
			logErr(err);
			showToast("获取方案详情失败，请重试..")
		}
		)
	}
	
	function fomcatBonus(b){
		if(b == null || b.length < 1 || b == '0' || b == '0.0' || b == '0.00'){
			return '';
		}
		return b;
	}
})