<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <base href="<%=basePath%>">
    <link rel="shortcut icon" href="icon.ico" type="image/x-icon" />
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
    <meta name="description" content="">
    <meta name="author" content="">

    <title>HPtree Server</title>
    <!-- bootstrap -->
    <link href="css/bootstrap-theme.min.css" rel="stylesheet">
    <link href="css/bootstrap.min.css" rel="stylesheet">
    <script src="js/jquery-2.1.1.min.js"></script>
    <script src="js/bootstrap.min.js"></script>
    <style type="text/css">
      body {
              padding-top: 80px;
            }
      .result {
      	color: blue;
      }
      .warn {
      	color: red;
      }
    </style>
	<script type="text/javascript">
		/* 检查电子邮件格式 */
		function check_email(thiz) {
			var value = $(thiz).val();
			var filter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
			if (filter.test(value)) {
				$("#InputEmail").removeClass("has-error");
				$("#InputEmail").addClass("has-success");
			} else {
				$("#InputEmail").removeClass("has-success");
				$("#InputEmail").addClass("has-error");
			}
		}
		/* 检查上传的文件格式 */
	  	function check(thiz){
	  		var value = $(thiz).val();
	  		value = value.substring(value.lastIndexOf(".")+1);
	  		if (value.indexOf("zip") == -1) {
	  			document.getElementById("form").reset();
	  			alert("Please insure file format (.zip)!");
	  		}
	  	}
		
    	/* 开启服务器及Hadoop集群 */
    	function start_hadoops() {
    		$('#hadoops_status').css("color", "green");
    		$('#hadoops_status').html('<span class="glyphicon glyphicon-forward" aria-hidden="true"></span> Hadoop is starting ... please wait about 40 seconds');
    		$.ajax({
                url: "start_hadoops.do",
                type: "GET",
                dataType: "text",
                success: function(data){
                	if (data == "success") {
                		$('#hadoops_status').html('<span class="glyphicon glyphicon-ok-circle" aria-hidden="true"></span> Hadoop is running ...');
                	} else {
                		$('#hadoops_status').css("color", "red");
                		$('#hadoops_status').html('<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span> Hadoop starting fail, please contact: shixiangwan@foxmail.com');
                	}
                },
                error: function(data){
            		$('#hadoops_status').css("color", "red");
            		$('#hadoops_status').html('<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span> Hadoop starting fail, please contact: shixiangwan@foxmail.com');
                }
            }); 
    	}
    	/* 检查服务器及Hadoop集群运行状态 */
    	$(document).ready(function(e) {
    	    $('#check_hadoops').click();
    	});
    	function check_status() {
    		$.ajax({
                url: "check_status.do",
                type: "GET",
                dataType: "text",
                success: function(data){
                	if (data == "running") {
                		$('#hadoops_status').css("color", "green");
                		$('#hadoops_status').html('<span class="glyphicon glyphicon-ok-circle" aria-hidden="true"></span> Hadoop is running ... ');
                	} else {
                		$('#hadoops_status').html('<span class="glyphicon glyphicon-remove-circle" aria-hidden="true"></span> Hadoop is stopped, please start hadoop first');
                	}
                },
                error: function(data){ }
            });
    	}
	</script>
  </head>

  <body>
	<!-- 引入导航栏 -->
    <jsp:include page="header.jsp" />

    <div class="container">
		<div class="panel panel-default">
		  <div class="panel-heading">
		    <h2 class="panel-title">
		    	<span class="glyphicon glyphicon-hourglass" aria-hidden="true"></span>
		    	HPtree Server
		    </h2>
		  </div>
		  <div class="panel-body">
			<form id="form" action="predict.do" method="post" enctype="multipart/form-data">
				<label>
				    <input type="radio" name="name" id="optionsRadios1" value="option1" checked>
				    Upload zip fasta DNA/RNA file (.zip required)
				    &nbsp;&nbsp;&nbsp;&nbsp;<a href="download_example.do">example.zip</a>
				    <input type="file" name="file" onchange="check(this)">
				</label>
				<p style="height:1px;"></p>
				<p>Note: maximum file size is 20MB. 
					<span class="warn" id="count">${requestScope.error}</span>
				</p>
				<p style="height:1px;"></p>
				<button id="check_hadoops" type="button" onclick="check_status()" style="display:none"></button>
				<div class="has-success" id="InputEmail">
					<input type="email" name="email" class="form-control" style="width:404px;height:30px;" onchange="check_email(this)" placeholder="enter your email, copy of result will be sent (optional)">
				</div>
				<p style="height:1px;"></p>
				<button id="" type="button" onclick="start_hadoops()" class="btn btn-info btn-center" style="width:200px;">
					<span class="glyphicon glyphicon-off" aria-hidden="true"></span>
					Start Hadoop
				</button>
				<button type="submit" class="btn btn-info btn-center" style="width:200px;">
					<span class="glyphicon glyphicon-ok" aria-hidden="true"></span>
					Submit
				</button>
				<p style="height:1px;"></p>
				<span id="hadoops_status" style="color:orange;"></span>
			</form>
			<p style="height:1px;"></p>
			<p><strong>Hadoop Phylogenetic Tree: </strong><br>
				Hadoop Phylogenetic Tree is a package of multi-platform Java software tools, which aimed at constructing phylogenetic tree on large scale multiple similar DNA/RNA sequence alignment output.
			</p>
		  </div>
		</div>
		
		<!-- Records of your experiment -->
		<div class="panel panel-default">
		  <div class="panel-heading">
		      <h2 class="panel-title">
		    	 <span class="glyphicon glyphicon-th" aria-hidden="true"></span>
		    	 Running Results
	    	  </h2>
		  </div>
		  <div class="panel-body">
			  <c:if test="${empty requestScope.time}">
				<p>When hadoop is running, wait for a while patiently, and <span style="color:orange;">do not</span> refresh this page.</p>
				<p>Thank you for using our service. </p>
			  </c:if>
			  <c:if test="${not empty requestScope.time}">
				<p>*<strong>Job ID:</strong> ${requestScope.time}</p>
				<%-- <p>*<strong>Your Detailed Results:</strong> 
				&nbsp;&nbsp;<a href="download.do?time=${requestScope.time}">Download</a>
				</p> --%>
				<p>*<strong>Phylogenetic tree:</strong>
				<img src="./tree/tree.svg" class="img-responsive" alt="Responsive image">
			  </c:if>
		  </div><!-- /panel-body -->
		</div>
    </div> <!-- /container -->
  </body>
</html>