<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
  <%@taglib  prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
  
  <c:set  var="menu_id"            value="${ map.menu_id           }" /> 
  <c:set  var="startnum"           value="${ searchVo.pdspagination.startPage         }" /> 
  <c:set  var="endnum"             value="${ searchVo.pdspagination.endPage           }" /> 
  <c:set  var="totalpagecount"     value="${ searchVo.pdspagination.totalPageCount  }" /> 
 
  <div id="paging" style="margin: 20px 0px; border:0px solid black;">
    <table style="width:40%;height:25px;text-align:center"  >
     <tr>
       <td style="width:40%;height:25px;text-align:center" >
     
     <!-- 처음/ 이전 -->     
     <c:if test="${ startnum gt 1 }">
       <a href="/Pds/List?menu_id=${ menu_id }&nowpage=1">⏮</a>
       <a href="/Pds/List?menu_id=${ menu_id }&nowpage=${ startnum - 1 }">
       ⏪
       </a>
     </c:if>
     
     <!-- 1 2 3 4 5 6 [7] 8 9 10  -->
     <c:forEach  var="pagenum"  begin="${startnum}"  end="${endnum}"  step="1">
        <a href="/Pds/List?menu_id=${ menu_id }&nowpage=${ pagenum }">
        ${ pagenum }
        </a>&nbsp;&nbsp;     
     </c:forEach>    
     
     <!-- 다음 / 마지막 -->
     <c:if test="${ totalpagecount ne endnum }">
       <a href="/Pds/List?menu_id=${ menu_id }&nowpage=${ endnum + 1 }">
       ⏩
       </a>
       <a href="/Pds/List?menu_id=${ menu_id }&nowpage=${ totalpagecount }">⏭</a>
     </c:if>    
     
      </td>
     </tr>    
    </table>   
  </div>
  
  
  
  
  
  
  
  
  