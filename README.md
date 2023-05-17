# 收货地址智能解析

## 简介
用于根据文本自动解析出收货信息。 \
部分解析逻辑参考了：https://github.com/wzc570738205/smartParsePro

## 在线演示
[演示地址](http://114.132.48.33/)

## 更新日志
### 2.0(2023-03-03)
- 重构代码，对比1.*能过滤更多的特殊字符


## 使用
### 解析用户地址信息-parseUserInfo
```java

class SmartParseTest {
    @Test
    void parseUserInfo() {

        SmartParse smartParse = new SmartParse(new LocalDataAddressDataLoader());

        List<String> textList = new ArrayList<>();
        textList.add("四川南充市营山县城南镇顺安街60号楼1901 15521950357 彭大哥");
        textList.add("四川省凉山市会理县民族实验中学黎溪镇中厂村9组");
        textList.add("四川省凉山州市德昌县德州镇角半七社");
        textList.add("萧，13021121177，[黑龙江哈尔滨市南岗区哈西街道]巴黎第九区16-1-1001");
        textList.add("王婷婷 ，18639117616 ， ，河南省 许昌市 长葛市 明珠花园10号楼101室");
        textList.add("王婷婷 ，18639712616 ， ，河南-新乡市-长垣市-恼里镇10号楼101室");
        textList.add("收货人: 唐菊\n" +
                "手机号码: 15508153912\n" +
                "所在地区: 四川省南充市营山县城南街道\n" +
                "详细地址: 宝珍花园顺安街21号楼801");
        for (String text : textList) {
            UserInfo userInfo = smartParse.parseUserInfo(text);
            System.out.println(JSONUtil.toJsonStr(userInfo));
        }
    }
    
}
```
输出结果
```text
{"address":"镇顺安街60号楼1901","city":"南充市","provinceCode":"34122","cityCode":"34280","streetCode":"34328","mobile":"15521950357","county":"营山县","countyCode":"34325","province":"四川省","street":"城南街道","name":"彭大哥"}
{"address":"四川省凉山市会理县民族实验中学黎溪镇中厂村9组","city":"凉山彝族自治州","provinceCode":"34122","cityCode":"35947","county":"会理市","countyCode":"36086","province":"四川省"}
{"address":"四川省凉山州市德昌县德州镇角半七社","city":"凉山彝族自治州","provinceCode":"34122","cityCode":"35947","county":"德昌县","countyCode":"36198","province":"四川省"}
{"address":"]巴黎第九区16-1-1001","city":"哈尔滨市","provinceCode":"5845","cityCode":"6215","streetCode":"6459","mobile":"13021121177","county":"南岗区","countyCode":"6447","province":"黑龙江省","street":"哈西街道","name":"萧"}
{"address":"明珠花园10号楼101室","city":"许昌市","provinceCode":"1","cityCode":"360","mobile":"18639117616","county":"长葛市","countyCode":"420","province":"河南省","name":"王婷婷"}
{"address":"10号楼101室","city":"新乡市","provinceCode":"1","cityCode":"2336","streetCode":"2441","mobile":"18639712616","county":"长垣市","countyCode":"2440","province":"河南省","street":"恼里镇","name":"王婷婷"}
{"address":"宝珍花园顺安街28号楼701","city":"南充市","provinceCode":"34122","cityCode":"34280","streetCode":"34328","mobile":"15508153912","county":"营山县","countyCode":"34325","province":"四川省","street":"城南街道","name":"唐菊"}
```

### 解析地址信息-parseAddressInfo
此方法只解析地址信息，不会解析姓名和手机电话

## 数据来源-AddressDataLoader
数据来源提供了可扩展接口，可以根据自己项目的地址数据加载解析扩展，也可以使用默认提供的 *LocalDataAddressDataLoader*
- LocalDataAddressDataLoader会自动加载resources/areaData.json数据源，此数据与2022年1月左右在高德开发平台采集
### 扩展示例
```java
import java.util.List;

@Configuration
public class ApplicationConfig {

    @Bean
    public SmartParse smartParse(AddressDataLoader addressDataLoader) {
        return new SmartParse(addressDataLoader);
    }

    @Bean
    @Autowired
    public AddressDataLoader addressDataLoader(AreaService areaService) {
        return new AddressDataLoader() {
            private final Cache<Integer, List<Address>> cache = CacheUtil.newFIFOCache(1);

            @Override
            public List<Address> loadData() {
                List<Address> data;
                // ...
                return data;
            }
        };
    }

}
```




## 联系作者
* Email：320678191@qq.com
* QQ: 320678191
