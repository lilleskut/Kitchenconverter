package com.example.jens.kitchenconverter;

import java.math.BigInteger;

/*
    rational class
 */
public class Rational {

    private int numerator;
    private int denominator;

    public Rational(int num, int den) {
        this.numerator = num/gcdThing(num,den);
        this.denominator = den/gcdThing(num,den);
    };

    public int getNumerator() { return numerator; }

    public int getDenominator() { return denominator; }

    public Rational(Double d) {
        String s = String.valueOf(d);
        int digitsDec = s.length() -1 -s.indexOf('.');

        int den =1;
        for(int i = 0; i < digitsDec; i++) {
            d *= 10;
            den *=10;
        }
        int num = (int) Math.round(d);

        this.numerator = num/gcdThing(num,den);
        this.denominator = den/gcdThing(num,den);
    }

    // assume that string is decimal (1.5) or simple fraction (3/2) or mixed fraction (1 1/2)
    public Rational(String s) {
        int num =1;
        int den =1;

        if(!s.contains("/")) { // decimal number
            Double d = Double.valueOf(s);
            int digitsDec = s.length() -1 -s.indexOf('.');

            den =1;
            for(int i = 0; i < digitsDec; i++) {
                d *= 10;
                den *=10;
            }
            num = (int) Math.round(d);

        } else { // fraction or mixed fraction
            String[] numbers = s.split("[ /]");

            switch(numbers.length) {
                case 2: // true fraction
                    num = Integer.valueOf(numbers[0]);
                    den = Integer.valueOf(numbers[1]);
                    this.numerator = num/gcdThing(num,den);
                    this.denominator = den/gcdThing(num,den);
                break;
                case 3: // mixed fraction
                    num = Integer.valueOf(numbers[1]) + Integer.valueOf(numbers[0]) * Integer.valueOf(numbers[2]);
                    den = Integer.valueOf(numbers[2]);
                break;
            }
        }
        this.numerator = num/gcdThing(num,den);
        this.denominator = den/gcdThing(num,den);
    }

    // return fraction string
    public String toString() {
        if( denominator==1 ) {
            return String.valueOf(numerator);
        } else {
            return String.valueOf(numerator) + "/" + String.valueOf(denominator);
        }
    }

    // return double value of fraction
    public Double toDouble() { return ((double) numerator)/denominator; }

    // reciprocal
    public Rational reciprocal () {
        return new Rational(denominator,numerator);
    }

    // multiply
    public Rational multiply (Rational rat2) {
        int num = numerator * rat2.getNumerator();
        int den = denominator * rat2.getDenominator();
        return new Rational(num,den);
    }

    // divide
    public Rational divide (Rational rat2) {
        return multiply(rat2.reciprocal());
    }

    //validate string (allow fractions, mixed fractions and decimals)
    public static boolean validFraction(String s) {
        String regex = "\\d{1,5}([.]\\d{1,3}|(\\s\\d{1,5})?[/]\\d{1,3})?";
        return s.matches(regex);
    }

    // GCD
    private static int gcdThing(int a, int b) {
        BigInteger b1 = BigInteger.valueOf(a);
        BigInteger b2 = BigInteger.valueOf(b);
        BigInteger gcd = b1.gcd(b2);
        return gcd.intValue();
    }

}
