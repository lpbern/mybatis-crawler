package com.mybatis.crawler;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CrawlerMapper {

    @Select("select * from crawlerlink where phonenumber is null order by id asc limit 50;")
    List<CrawlerLink> findPerFiftyThatHasNoPhoneNumber();


    @Insert("insert into crawlerlink(createtime, pageurl) values(#{createtime},#{pageurl})")
    void insert(CrawlerLink crawlLink);

    @Update("update crawlerlink set phonenumber=#{phonenumber} where id=#{id}")
    void update(CrawlerLink crawlLink);



}
