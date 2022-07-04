package ${basePackage}.controller;

import ${basePackage}.common.CommonResponse;
import ${basePackage}.common.ResponseUtil;
import ${basePackage}.model.${modelNameUpperCamel};
import ${basePackage}.service.${modelNameUpperCamel}Service;
import ${basePackage}.common.CommonResponse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by ${author} on ${date}.
 */

@RestController
@RequestMapping("${baseRequestMapping}")
public class ${modelNameUpperCamel}Controller {

    @Autowired
    private ${modelNameUpperCamel}Service ${modelNameLowerCamel}Service;

    @PostMapping
    public CommonResponse add(@RequestBody ${modelNameUpperCamel} ${modelNameLowerCamel}) {
        return ResponseUtil.generateResponse(${modelNameLowerCamel}Service.save(${modelNameLowerCamel}));
    }

    @DeleteMapping("/{id}")
    public CommonResponse delete(@PathVariable Integer id) {
        return ResponseUtil.generateResponse(${modelNameLowerCamel}Service.deleteById(id));
    }

    @PutMapping
    public CommonResponse update(@RequestBody ${modelNameUpperCamel} ${modelNameLowerCamel}) {
        return ResponseUtil.generateResponse(${modelNameLowerCamel}Service.update(${modelNameLowerCamel}));
    }

    @GetMapping("/{id}")
    public CommonResponse detail(@PathVariable Integer id) {
        return ResponseUtil.generateResponse(${modelNameLowerCamel}Service.getById(id));
    }

    @GetMapping
    public CommonResponse list(@RequestParam(defaultValue = "0") Integer pageNum, @RequestParam(defaultValue = "0") Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<${modelNameUpperCamel}> list = ${modelNameLowerCamel}Service.getAll();
        PageInfo<${modelNameUpperCamel}> pageInfo = new PageInfo<>(list);
        return ResponseUtil.generateResponse(pageInfo);
    }
}