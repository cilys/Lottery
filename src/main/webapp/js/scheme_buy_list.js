$(document).ready(function(){
	var BASE_URL = getHost() + "order/";
	
	var schemeId = getUrlParam("id");
	
	var startAllAppoint = 0;	//开始页数
	var currentPage = 1;	//当前页数
	var pageSize = 10
	var totalPage = 0;			//数据总条数
	var status = "";
	
	init();
	
	function init(){
		layui.use('form', function() {
			var form = layui.form;
			form.on('submit(search)', function(data) {
				getDatas();
				
				return false;
			});
			
			form.on('select(payStatus)', function(data) {
				currentPage = 1;
				
				return false;
			});
			form.on('select(payType)', function(data) {
				currentPage = 1;
				
				return false;
			});
			
			form.on('submit(calculate)', function(data) {				
				return false;
			});
			
			form.on('submit(enterToUser)', function(data) {				
				return false;
			});
		});
	}
	
	getDatas()
	
	
	function getDatas(){
		post(BASE_URL + "query",
		{
			pageNumber: currentPage
			, schemeId : schemeId
			, pageSize : pageSize
			, orderStatus : $("#select_pay_status").val()
			, payType : $("#select_pay_type").val()
		}, function success(res){
			showToast(res.msg);
			if(res.code == 0){
				setDataToView(res.data.list);
				totalPage = res.data.totalRow;
				currentPage = res.data.pageNumber;
				toPage();
			}
		}, function error(err){
			showToast("操作失败，请稍后重试..");
		});
	}
	
	function setDataToView(data){
		var s = "<thead>"
				+ 	"<tr>"
				+		"<th>客户姓名</th>"
				+		"<th>购买份额</th>"
				+		"<th>支付方式</th>"
				+		"<th>订单状态</th>"
				+		"<th>操作者</th>"
				+		"<th>投资比例</th>"
				+		"<th>所得奖金</th>"
				+		"<th>奖金状态</th>"
				+		"<th>创建时间</th>"
				+		"<th>操作</th>"
				+	"</tr>"
				+ "</thead>";
		
		if(data.length > 0){
			
			s += "<tbody>";
			
			$.each(data, function(v, o) {
				s += "<tr>";
				
				s += 	"<td>" + strFomcat(o.cusertomerName) + "</td>";
				s += 	"<td>" + o.money + "</td>";
				s += 	"<td>" + fomcatPayType(o.payType) + "</td>";
				s += 	"<td>" + fomcatPayStatus(o.orderStatus) + "</td>"
				s += 	"<td>" + strFomcat(o.operatorName) + "</td>"
				s += 	"<td>" + fomcatRate(o.payedRate) + "</td>"
				s += 	"<td>" + strFomcat(o.bonusMoney) + "</td>"
				s += 	"<td>" + fomcatBonusStatus(o.bonusStatus) + "</td>"
				s += "<td>" + o.createTime + "</td>";
				
				s +=	"<td>"
						+	"<div class='layui-btn-group'>"
						+		"<button class='layui-btn layui-btn-primary layui-btn-sm' id='btn_edit' data-id='" + o.id + "'>"
						+			"<i class='layui-icon'>" + fomcatPayStatusIcon(o.orderStatus) + "</i>"
						+		"</button>"
						+	"</div>"
						+"</td>"
						
				s += 	"</tr>";
			});
			
			s += "</tbody>"
			
			$("#t_datas").html(s)
			
			
			$("#t_datas #btn_edit").on('click', function(){
				var id = $(this).attr("data-id");
				layer.open({
					type : 1,
					title : '修改订单状态',
					shadeClose : true,
					area : ['316px', '240px'],
					resize : false,
					content : doubleBtnInputDialogHtml()
				});
				$("#btn_pay").on("click", function(){
					updatePayStatus(id, '0');
//					layer.closeAll()
				});
				$("#btn_unpay").on("click", function(){
					updatePayStatus(id, '1');
//					layer.closeAll()
				});
				$("#btn_back").on("click", function(){
					updatePayStatus(id, '2');
//					layer.closeAll()
				});
			})
            $("#paged").show();
		} else{
			$("#paged").hide();
			$("#t_datas").html("<br/><span style='width:10%;height:30px;display:block;margin:0 auto;'>暂无数据</span>");
		}
	}
	
	function toPage(){
   		layui.use('laypage', function(){
   			var laypage = layui.laypage;
   			
   			laypage.render({
	   			elem : 'paged'
	   			, count : totalPage
	   			, limit : pageSize
	   			, curr : currentPage
	   			, jump : function(obj, first){
	   				currentPage = obj.curr;
	   				
	   				if(!first){
	   					getDatas();
	   				}
	   			}
	   		});
   		});
	};
	
	function delById(id){
		post(BASE_URL + "delById", {
			id : id
		}, function success(res){
			showToast(res.msg);
			
			if(res.code == 0){
				getDatas();
			}
		}, function error(err){
			showToast("删除失败，请重试...");
		})
	}
	
	function updatePayStatus(id, status){
		post(getHost() + "sys/order/updatePayStatus",
		{
			id : id
			, orderStatus : status
		}, function success(res){
			showToast(res.msg);
			if(res.code == 0){
				layer.closeAll();
				reloadParent();
			}
		}, function error(err){
			showToast("更新状态失败，请重试..");
		})
	}
	
	function fomcatBonusStatus(status){
		if(status == null || status == undefined){
			status = "";
		}
		if(status == "0"){
			return "已到账"
		} else if(status == "1"){
			return "已计算"
		} else if(status == "2"){
			return "退还系统";
		} else if(status == "3"){
			return "待处理";
		}
		return status;
	}
	
	function fomcatRate(rate){
		if(status == null || status == undefined){
			status = "0.00%";
		}
		return rate + "%";
	}
	
	$("#btn_calculate").on("click", function(){
		queryBonusStatus(schemeId)
	})
	$("#btn_enter_to_user").on("click", function(){
		queryBonusStatus(schemeId)
	})
	
	function calculateBonus(schemeId){
		post(getHost() + "sys/order/calculateBonus",
		{
			schemeId : schemeId
		}, function success(res){
			showToast(res.msg);
		}, function error(err){
			logErr(err)
			showToast("计算奖金失败，请重试..")
		})
	}
	
	function distributionBonus(schemeId){
		post(getHost() + "sys/userMoneyFlow/distributionBonus",
		{
			schemeId : schemeId
		}, function success(res){
			if(res.code == 0){
				showToast("分配方案已提交，请耐心等待系统处理")
			}else{
				showToast(res.msg);
			}
			
		}, function error(err){
			logErr(err)
			showToast("分配奖金失败，请重试..")
		})
	}
	
	function queryBonusStatus(schemeId){
		post(getHost() + "sys/scheme/queryBonusStatus", 
		{
			id : schemeId
		}, function success(res){
			if(res.code == 0){
				var bonusStatus = res.data;
				if(bonusStatus == 0){
					showToast("该方案的奖金已下发给客户，无法修改奖金状态")
				}else if(bonusStatus == -1){
					showToast("该方案无人购买，无需修改奖金状态")
				}else if(bonusStatus == 1){
					//1奖金已计算，未分配
					showBonusStatusDialog(schemeId)
				}else if(bonusStatus == 2){
					//奖金被退回
				}else if(bonusStatus == 3){
					//奖金未计算
					layer.confirm("是否计算奖金？", function(){
						calculateBonus(schemeId);
					});
				}
			}else{
				showToast(res.msg);
			}
		}, function error(err){
			logErr(err)
			showToast("查询奖金状态失败，请重试..")
		});
	}
	
	function showBonusStatusDialog(schemeId){
		layer.confirm("是否计算或分配奖金？", {btn:['重新计算', '奖金分配']}, function(){
			calculateBonus(schemeId);
		}, function(){
			distributionBonus(schemeId);
		});
	}
})