$(document).ready(function() {

	var params = getUrlParam("params");

	var cash = JSON.parse(params);
	
	var currentStatus = cash['status'];
	var cashId = cash['id'];
	
	$("#input_realName").val(cash['realName']);
	$("#input_money").val(cash['money']);
	$("#input_applyTime").val(cash['applyTime']);
	$("#input_msg").html(cash['msg']);
	$("#input_operatorName").val(getRealName());
	$("#input_status").val(fomcatCashStatus(currentStatus));
	
	layui.use('form', function() {
		var form = layui.form;
		form.on('submit(success)', function(data) {
			var jsonData = getFormJsonData("form_data");
			jsonData.operator = getUserId();
			jsonData.status = 0 + "";
			jsonData.id = cashId + "";
			
			if(currentStatus == 0){
				showToast("该申请已被审批");
				return false;
			} else if(currentStatus == 1){
				showCommitDialog("此操作可能会产生金钱交易，请确认是否同意该申请？同意后将无法再修改", jsonData);
			} else if(currentStatus == 2){
				showCommitDialog("是否继续拒绝该申请？", jsonData);
			}
			
			return false;
		});
		
		
		form.on('submit(refuse)', function(data) {
			var jsonData = getFormJsonData("form_data");
			jsonData.operator = getUserId();
			jsonData.status = 2 + "";
			jsonData.id = cashId + "";
			
			if(currentStatus == 2){
				showToast("该申请已被审批");
				return false;
			} else if(currentStatus == 0){
				showToast("该申请已被同意")
			} else if(currentStatus == 1){
				showCommitDialog("此操作可能会产生金钱交易，请确认是否继续拒绝该申请？", jsonData);
			}
			
			return false;
		});
	});
	
	function showCommitDialog(title, jsonData){
		layer.confirm(title ,function(index){
			updateStatus(jsonData)
		});
	}
	
	function updateStatus(jsonData){
		postBody(getHost() + "sys/cash/updateStatus", jsonData, 
		function success(res){
			showToast(res.msg);
			if(res.code == 0){
				dismissDialog();
				reloadParent();
			}
		}, function error(err){
			logErr(err)
			showToast("修改失败，请稍后重试..")
		});
	}

	
	function fomcatCashStatus(status){
		if(status == 0) {
			return "已同意";
		} else if(status == 1){
			return "待审批";
		} else if(status == 2){
			return "已驳回";
		}
		return status;
	}
})