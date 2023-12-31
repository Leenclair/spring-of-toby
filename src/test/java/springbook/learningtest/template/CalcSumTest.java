package springbook.learningtest.template;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class CalcSumTest {
    Calculator calculator;
    String numFilepath;

    @Before
    public void setUp(){
        this.calculator = new Calculator();
        this.numFilepath = getClass().getResource("/numbers.txt").getPath();
    }

    @Test
    public void sumOfNumbers() throws IOException{
        Assertions.assertThat(calculator.calcSum(this.numFilepath)).isEqualTo(10);
    }

    @Test
    public void multiplyOfNumbers() throws IOException{
        Assertions.assertThat(calculator.calcMultiply(this.numFilepath)).isEqualTo(24);
    }

    @Test
    public void concatenateStrings() throws IOException{
        Assertions.assertThat(calculator.concatenate(this.numFilepath)).isEqualTo("1234");
    }
}
