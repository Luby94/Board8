package com.board.pds.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PdsPagingResponse<T> {

	// 현재페이지에 보여줄 오라클 자료 : 10줄
    private List<T> pdsList = new ArrayList<>();
    
    // 아래의 paging.jsp 에서 사용할 변수들
    private PdsPagination pdspagination;

    public PdsPagingResponse(List<T> pdsList, PdsPagination pdspagination) {
        this.pdsList.addAll(pdsList);
        this.pdspagination = pdspagination;
    }

}
