<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="icon" type="image/png" href="/img/favicon.png" />
<link rel="stylesheet"	href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" />
<link rel="stylesheet"  href="/css/common.css" />
<style>
  
   #table {
     width: 800px;
     margin-bottom : 200px;      
     td {
	      text-align :center;
	      padding :10px;
	      
	      input[type=text]   { width : 100%;  }
	      textarea           { width : 100%;  height:250px; }
	      
	      &:nth-of-type(1) { 
	          width            : 150px; 
	          background-color : black;
	          color            : white; 
	      }
	      &:nth-of-type(2) { width : 250px;  }
	      &:nth-of-type(3) { 
	          width            : 150px; 
	          background-color : black;
	          color            : white;
	      }
	      &:nth-of-type(4) { width : 250px;  }    
     }
     
     tr:nth-of-type(3) td:nth-of-type(2) {
         text-align: left;
     }      
     
	 tr:nth-of-type(4) td[colspan] {
	        height : 250px;
	        width  : 600px;   
	        text-align: left;
	        vertical-align: baseline;
	 }
	 tr:last-child td {
	        background-color : white;
	        color            : black;   
	 }
   }
      
</style>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/browser-scss@1.0.3/dist/browser-scss.min.js"></script>

<script src="https://code.jquery.com/jquery.min.js"></script>
<script>
  $( function() {
	  
	  let num = 1;
	  $('#btnAddFile').on('click', function(e) {
		  
		  let tag = '<input type="file" name="upfile"' + ' class="upfile" multiple /><br>';
		  $('#tdfile').append(tag);
		  num++;
		  
	  })
	  
	  // ❌ 가 클릭되면
	  $('.aDelete').on('click', function(e) {
		  
		  e.preventDefault();		// a tag 기본기능(이벤트)을 무시 → href 페이지로 이동하지 않는다
		  e.stopPropagation();
		  
		  let aDelete = this;		// 내가 클릭한 a tag 가 this
		  console.dir(aDelete);
		  
		  $.ajax({
			  url : this.href,
			  method : 'GET'
		  })
		  .done( function( result ) {
			  console.log( result );
			  alert('삭제완료');
			  $(aDelete).parent().remove();		// aDelete 의 부모(parent) 를 찾아서 삭제(remove) → 화면에서 항목삭제
		  })
		  .fail( function( error ) {
			  console.dir(error);
			  alert('오류발생: ' + error);
		  })
	  })
  })
  
  // html input type="file" name="upload" multiple />
  // 여러파일을 선택하여 보낼 수 있다 + ctrl 이나 shift 여러개 선택
</script>

</head>
<body>
  <main>
    
    <%@include file="/WEB-INF/include/pdspagingmenus.jsp" %>
  
	<h2>자료실 내용 수정</h2>
	<form  action="/Pds/Update"  method="POST" enctype="multipart/form-data">
		<input type="hidden"  name="bno"     value="${ map.bno }" /><!-- 컨트롤러 전송을 위해 반드시 name 이 있어야함 → input type="hidden" -->
		<input type="hidden"  name="menu_id" value="${ map.menu_id }" />  
		<input type="hidden"  name="nowpage" value="${ map.nowpage }" />  
	<table id="table">
	 <tr>
	   <td>글번호</td>
	   <td>${ vo.bno }</td>
	   <td>조회수</td>
	   <td>${ vo.hit }</td>	   
	 </tr>
	 <tr>
	   <td>작성자</td>
	   <td>${ vo.writer }</td>
	   <td>작성일</td>
	   <td>${ vo.regdate }</td>
	 </tr>
	 <tr>
	   <td>제목</td>
	   <td colspan="3">
	   <input type="text" name="title" value="${ vo.title }" />	   
	   </td>	
	 </tr>
	 <tr>
	   <td>내용</td>
	   <td colspan="3">
	   <textarea name="content">${ vo.content }</textarea>
	   </td>
	 </tr>
	 
	 <tr>
	   <td>파일</td>
	   <td id="tdfile" colspan="3">
	   <div>
	   <c:forEach var="file" items="${ fileList }">
	     <div class="text-start">
	     	<a class="aDelete" href="/deleteFile?file_num=${ file.file_num }">❌</a>	<!-- file_num : primary key → 얘만 있어도 삭제됨 -->
	     	<%-- <a class="aDelete" href="/deleteFile/${file.file_num}">❌</a> --%>	<!-- file_num : primary key → 얘만 있어도 삭제됨 -->
	     	<a href="/Pds/filedownload/${ file.file_num }">${ file.filename }</a>
	     </div>
	   </c:forEach>
	   </div>
	   <hr>
	   <!-- 새 파일 추가 -->
	   <input type="button" id="btnAddFile" value="파일추가(100MB)" />
	   <input type="file" name="upfile" class="upfile" multiple />
	   </td>
	 </tr>
	 
	 <tr>
	 	<td colspan="4">
	 		<input type="submit" value="수정" class="btn btn-info" />
	 		<a class="btn btn-secondary" href="/Pds/List?menu_id=${map.menu_id}&nowpage=${map.nowpage}">목록</a>
	 	</td>
	 </tr>

	</table>	
   </form>   
	
  </main>
  
  <script>
  	  
  </script>
  
</body>
</html>





