package com.example.ch07jpastart4.domain.constant;

public enum ProductType {
    COMPUTER("컴퓨터"),
    LIFE("생활용품"),
    ACCESSORY("악세서리");

    private String type;

    private ProductType(String type){
        this.type = type;
    }

    public String getType(){
        return this.type;
    }
}
