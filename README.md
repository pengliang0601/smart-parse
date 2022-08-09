# 收货地址智能解析

## 简介
用于根据文本自动解析出收货信息。 \
部分解析逻辑参考了：https://github.com/wzc570738205/smartParsePro

## 使用
### 解析用户地址信息-parseUserInfo
```java

class SmartParseTest {
    @Test
    void parseUserInfo() {

        SmartParse smartParse = new SmartParse(new LocalDataAddressDataLoader());
        
        List<String> textList = new ArrayList<>();
        textList.add("姓名：彭大哥 电话：15521950357 地址：四川南充市营山县城南镇顺安街60号楼1901");
        textList.add("姓名：彭大哥 电话：15521950357 所在地区: 四川南充市营山县城南镇 详细地址: 顺安街60号楼1901");
        textList.add("收货人: 彭大哥 手机号码: 15521950357 所在地区: 四川南充市营山县城南镇 详细地址: 顺安街60号楼1901");
        textList.add("四川南充市营山县城南镇顺安街60号楼1901 15521950357 彭大哥");
        for (String text : textList) {
            UserInfo userInfo = smartParse.parseUserInfo(text);
            System.out.println(JSONUtil.toJsonStr(userInfo));
        }
    }
    
}
```
输出结果
```text
{"address":"城南镇顺安街60号楼1901","city":"南充市","mobile":"15521950357","county":"营山县","areaId":"34325","province":"四川省","name":"彭大哥"}
{"address":"城南镇顺安街60号楼1901","city":"南充市","mobile":"15521950357","county":"营山县","areaId":"34325","province":"四川省","name":"彭大哥"}
{"address":"城南镇顺安街60号楼1901","city":"南充市","mobile":"15521950357","county":"营山县","areaId":"34325","province":"四川省","name":"彭大哥"}
{"address":"城南镇顺安街60号楼1901","city":"南充市","mobile":"15521950357","county":"营山县","areaId":"34325","province":"四川省","name":"彭大哥"}
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
