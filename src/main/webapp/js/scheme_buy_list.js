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
				totalPage = res.data.totalPage;
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
				+		"<th>支付状态</th>"
				+		"<th>操作者姓名</th>"
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
				s += "<td>" + o.createTime + "</td>";
				
				s +=	"<td>"
						+	"<div class='layui-btn-group'>"
						+		"<button class='layui-btn layui-btn-primary layui-btn-sm' id='btn_edit' data-id='" + o.id + "'>"
						+			"<i class='layui-icon'>" + fomcatPayStatusIcon(o.orderStatus) + "</i>"
						+		"</button>"
						+		"<button class='layui-btn layui-btn-primary layui-btn-sm' id='btn_del' data-id='" + o.id + "'>"
						+			"<i class='layui-icon'>&#xe640;</i>"
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
			
			$("#t_datas #btn_del").on('click', function(){
				var userId = $(this).attr("data-id");
				
				layer.confirm('是否删除该用户？(此操作不可恢复，请谨慎操作！)',function(index){
					del(userId)
				});
			})
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
		post(BASE_URL + "updatePayStatus",
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
})