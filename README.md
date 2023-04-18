# Web Crawling Design with Java Springboot

- [x]  Assumptions Made based on targets
- [x]  Model for system architecture
- [x]  Database Design
- [x]  Reasoning behind proposed architecture
- [x]  Dependencies used
- [x]  Approach based on website
- [x]  Link to codebase
- [x]  Results

# Target:

50000 Phone Numbers retrieved from [chotot.com](http://chotot.com/) from a web crawler

# Assumption Made based on Targets:

1. An assumption is made that data retrieved from the web crawler would act as a data source for software application and is a persistent database.
2. An assumption is made that read and write speeds are important for the database and that solution needs to provide a continuous and reproducible application that can be deployed anywhere.
3. An assumption is made that duplicate phone number is acceptable but listings (based on url) must be unique as one phone number can list multiple items for sale.

# System Architecture

Covered system scope is highlighted in blue

![notion 1](https://user-images.githubusercontent.com/112774144/232819981-a0ea57ee-2b8a-4fe7-83b0-1d5b692bddc3.jpeg)
 

# Database Design

Any SQL Based DB would do but for the purpose of this project mysql is used

```sql
create table crawlerlink (
    id bigint auto_increment not null primary key,
	pageurl varchar(255) not null default '',
	createtime bigint not null default 0,
	phonenumber varchar(30) null
);
```

# Reasoning behind Tech Stack choice

1. Java springboot provides the flexibility and the reliability of enterprise grade APIs and a lot of well-maintained libraries for API and web-crawling
2. Any open source SQL database allows read and write based access control and serves as a reliable medium of data store
3. Take advantage of optimal multithreading in java springboot

# Java Dependencies:

1. Jsoup
2. Selenium
3. [https://mybatis.org/mybatis-3/](https://mybatis.org/mybatis-3/)

# Findings about the site:

1. Phone numbers are only in detail pages and is hidden with javascript click button process
2. In Product Listing site, it is organized by categories such as vehicles or household appliances
3. No Login required to see any listings to certain point

# Methodology:

1. [https://www.chotot.com/mua-ban](https://www.chotot.com/mua-ban) displays all product with no category. Thus it will give us the best chance to scrape for links to product details page. Simple `AdItem_adItem__gDDQT` search will give you the element for the product detail links. Since the page is paginated we can navigate by visiting each pagination [https://www.chotot.com/mua-ban?page=](https://www.chotot.com/mua-ban?page=2)

![notion2](https://user-images.githubusercontent.com/112774144/232820150-d5dd8396-28f8-41b5-8a9d-20792108fe35.png)
For this Jsoup is used as only static content is needed to get the product detail links.

2. There are instances where the product link is not complete as it is hosted in the same domain as the product list page. Which will cause errors when we are trying to navigate to the detail page
![notion3](https://user-images.githubusercontent.com/112774144/232820445-94d8c432-92ac-418d-adcd-8dd629ebf442.png)

- Simple approach is to clean the data and insert the domain as link
![notion4](https://user-images.githubusercontent.com/112774144/232820544-383bbb1d-a2ed-4076-9659-8e9feb8af2de.png)


1. SQL is used as a source of truth for the APIs, I developed 2 APIs which can both be hit simultaneously since multithreading is the default for springboot. One to parse and store the links from the homepage, one to parse the phone number using selenium.
    1. `/crawler/all/{pagination}` is to scrape data according to the page
    2. `/crawler/process` open product detail pages asynchronously for the crawler to scrape the data
 ![notion5](https://user-images.githubusercontent.com/112774144/232820670-f6c58b21-a655-4b69-ada2-cd05c3429e37.png)

    
    # Code:
    
    [GitHub - lpbern/mybatis-crawler](https://github.com/lpbern/mybatis-crawler/tree/master)
    
    # Sample Result:
    ![notion6](https://user-images.githubusercontent.com/112774144/232820722-bac92cd2-639f-473f-a27d-672bceb51cfe.png)

