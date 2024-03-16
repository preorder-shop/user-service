package com.example.userserver.common.mail;

public class CertificationNumber {

    public static String getCertificationNumber() {
        // 임의의 4자리 숫자 만들기
        String certificationNumber = "";

        for (int count = 0; count < 4; count++) {
            certificationNumber += (int) (Math.random() * 10); // 0 ~ 9
        }

        return certificationNumber;

    }

}
