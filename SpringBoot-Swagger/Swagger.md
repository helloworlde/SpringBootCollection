# Swagger 常用注解说明

| **注解** | **属性** | **值** | **备注** |
|---------|---------|------|------------|
| `@Api` | value | 字符串 | 可用在`class`头上,`class`描述 |
|        | description | 字符串 |  |
| | | | `@Api(value = "xxx", description = "xxx")` |
| `@ApiOperation` | value | 字符串 | 可用在方法头上.参数的描述容器 |
|                 | notes | 字符串 | 说明 |
|                 | httpMethod |字符串| 请求方法 |  
| | | | `@ApiOperation(value = "xxx", notes = "xxx", method = "GET")` |
| `@ApiImplicitParams` | {} | `@ApiImplicitParam`数组 | 可用在方法头上.参数的描述容器 |
| | | | `@ApiImplicitParams({@ApiImplicitParam1,@ApiImplicitParam2,...})` |
| `@ApiImplicitParam` | name         | 字符串 与参数命名对应  | 可用在`@ApiImplicitParams`里 |
|                     | value        | 字符串 | 参数中文描述 |
|                     | required     | 布尔值 | true/false |
|                     | dataType     | 字符串 | 参数类型 |
|                     | paramType    | 字符串 | 参数请求方式:query/path |
|                     |              |       | query:对应`@RequestParam`传递|
|                     |              |       | path: 对应`@PathVariable`{}path传递 |
| 					 |dataType|字符串| 参数类型|
|					 |dataTypeClass|类| 参数对应的类|
|                     | defaultValue | 字符串 | 在api测试中默认值 |
||| |     `            @ApiImplicitParam(name = "newProduct", value = "商品信息对象", required = true, dataType = "Product", dataTypeClass = Product.class)`
 |
| `@ApiResponses` | {} | `@ApiResponse`数组 | 可用在方法头上.参数的描述容器 |
| | | | `@ApiResponses({@ApiResponse1,@ApiResponse2,...})` |
| `@ApiResponse`      | code         | 整形   | 可用在`@ApiResponses`里 |
|                     | message      | 字符串 | 错误描述 |
|                     | response      | 类| 返回结果对应的类|
| | | | `@ApiResponse(code = 200, message = "Successful", response = CommonResponse.class)` |
| `@ApiModelProperty` | name         | 字符串   | 实体类参数名称 |
|                     | value      | 字符串 |实体类参数值 |
|                     | notes      | 字符串| 说明|
| | | | `@ApiModelProperty(name = "name", value = "name", notes = "名称")` |