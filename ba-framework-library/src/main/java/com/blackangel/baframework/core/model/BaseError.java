package com.blackangel.baframework.core.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 기본 에러 클래스
 * errCode 가 같다면 같은 에러로 인식한다.
 */

@EqualsAndHashCode(of = {"errCode"})
@AllArgsConstructor
@Setter
@Getter
@ToString(of = {"errCode", "errMessage"})
public class BaseError {

    private int errCode;
    private String errMessage;
    private Throwable throwable;    // null 일 수 있음. (예외가 던져진 게 아닌 케이스를 BaseError 로 잡아서 처리할때)
}
