package com.example.esdata;

import com.example.esdata.service.QueryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EsDataApplication.class)
public class EsDataApplicationTests {

    @Autowired
    private QueryService queryService;

    @Test
    public void contextLoads() {
        System.out.println(222);
        queryService.getAndOut();
        System.out.println(333);
    }

    @Test
    public void testQuery(){
        System.out.println(444);
        queryService.queryTest();
        System.out.println(555);
    }
}
