$(document).ready(function(){
	
	var userId = getUrlParam("userId");
	
	var startAllAppoint = 0;	//开始页数
	var currentPage = 1;	//当前页数
	var pageSize = 10
	var totalPage = 0;			//数据总条数
	var status = "0";
	
	getDatas()

	function getDatas(){
		post(getHost() + "sys/scheme/queryAll?pageNumber=" + currentPage + "&pageSize=" + pageSize + "&status=" + status,
		{
			pageNumber: currentPage
			, pageSize : pageSize
			, status : status
		}, function success(res){
			showToast(res.msg);
			if(res.code == 0){
				setDataToView(res.data.list);
				totalPage = res.data.totalRow;
				currentPage = res.data.pageNumber;
				toPage();
			}
		}, function error(err){
                showToast("获取列表失败");
		});
	}
	
	function setDataToView(data){
		var s = "<thead>"
				+ 	"<tr>"
				+ 		"<th>方案id</th>"
				+		"<th>方案名称</th>"
				+		"<th>总金额</th>"
				+		"<th>预购金额</th>"
				+		"<th>已付金额</th>"
				+		"<th>过期时间</th>"
				+		"<th>操作</th>"
				+	"</tr>"
				+ "</thead>";
		
		if(data.length > 0){
			
			s += "<tbody>";
			
			$.each(data, function(v, o) {
				s += "<tr>";
				
				s += 	"<td>" + o.id + "</td>";
				s += 	"<td>" + strFomcat(o.name) + "</td>";
				s += 	"<td>" + o.totalMoney + "</td>";
				s += 	"<td>" + o.selledMoney + "</td>"
                s += 	"<td>" + o.payedMoney + "</td>"
                s += 	"<td>" + strFomcat(o.outOfTime) + "</td>"
				
				
				s += "<td>"
					+	"<div class='layui-btn-group'>"
					+ 		"<button class='layui-btn layui-btn-primary layui-btn-sm' id='btn_pay' data-id='" + o.id + "' data-all='" + JSON.stringify(o) + "'>"
					+			"<i class='layui-icon'>&#xe654;</i>"
					+		"</button>"
					+	"</div>"
					+"</td>"
				
				s += 	"</tr>";
			});
			
			s += "</tbody>"
			
			$("#t_datas").html(s)
			
			$("#t_datas #btn_pay").on('click', function(){
				var id = $(this).attr("data-id");
				var data = JSON.parse($(this).attr("data-all"))
				
				$("#div_list").removeClass("layui-hide")
				$("#div_detail").removeClass("layui-hide");
				$("#div_list").addClass("layui-hide")
				
				$("#input_name").val(data.name);
				$("#input_total_money").val(data.totalMoney);
				$("#input_selled_money").val(data.selledMoney);
				$("#input_payed_money").val(data.payedMoney);
				$("#input_selled_money").val(data.selledMoney);
				
				
				$("#input_out_of_time").val(data.outOfTime);
				$("#textarea_descption").html(data.descption);
				
				$("#input_id").val(data.id);
				
			})

            $("#paged").show();
		} else{
			$("#paged").hide();
			$("#t_datas").html("<br/><span style='width:10%;height:30px;display:block;margin:0 auto;'>暂无数据</span>");
		}		
	}
	
	$("#btn_back").on("click", function(){
		$("#div_list").removeClass("layui-hide")
		$("#div_detail").removeClass("layui-hide");
		$("#div_detail").addClass("layui-hide")
		
		
	});
	$("#btn_add").on("click", function(){
		layui.use('form', function() {
			var form = layui.form;
			form.on('submit(add)', function(data) {
				var jsonData = getFormJsonData("form_data");
				jsonData.customerId = userId;
				jsonData.operator = getUserId();
				jsonData.schemeId = $("#input_id").val();
				
				
				log("购买参数：")
				log(jsonData)
				
				postBody(getHost() + "order/add", jsonData, function success(res){
					showToast(res.msg);
					if(res.code == 0){
						layer.confirm('购买成功',function(index){
			              	layer.close(index);
			              	dismissDialog();
			       			reloadParent();
			        	});
					}
				}, function error(err){
					logErr(err)
					showToast("");
				});
				
				return false;
			});
		});
	});
	
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
	   					getDatas()
	   				}
	   			}
	   		});
   		});
	};
})