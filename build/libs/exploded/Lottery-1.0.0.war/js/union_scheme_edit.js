$(document).ready(function(){
	var id = getUrlParam("id");
	
	queryById();
	
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
        	if (checkMoney(totalMoney)) {
        		
        	} else{
        		showToast("请输入正确的金额，最多两位小数!")
        		return false;
        	}
			
			jsonData.id = id;
			
			update(jsonData);
			
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
	
	function update(jsonData){
		postBody(getHost() + "sys/scheme/updateInfo?id=" + id, 
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
				$("#input_total_bonus").val(res.data.totalBonus);
				$("#input_bonus_rate").val(res.data.bonusRate);
				$("#input_can_use_bonus").val(res.data.canUseBonus);
				
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
})