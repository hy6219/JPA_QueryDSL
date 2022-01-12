package com.example.ch04jpastart3.common;

public enum RoleType {
    ADMIN("관리자"),USER("일반 사용자");

    private String s;

    RoleType(String s) {
        this.s = s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getS() {
        return s;
    }

    @Override
    public String toString() {
        return "RoleType{" +
                "s='" + s + '\'' +
                '}';
    }
}
