package cn.com.hellowood.mapper.service;

import cn.com.hellowood.mapper.common.CommonMapper;
import cn.com.hellowood.mapper.common.CommonService;
import cn.com.hellowood.mapper.utils.ServiceException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * The type Base service.
 *
 * @param <T> the type parameter
 */
public abstract class BaseService<T> implements CommonService<T> {

    /**
     * real class type of current generic
     */
    private Class<T> modelClass;

    @Autowired
    protected CommonMapper<T> commonMapper;

    @Autowired
    HttpServletRequest request;

    /**
     * Instantiates a new Base service.
     */
    public BaseService() {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.modelClass = (Class<T>) type.getActualTypeArguments()[0];
    }


    @Override
    @Transactional
    public Integer save(T model) {
        return commonMapper.insertSelective(model);
    }

    @Override
    @Transactional
    public Integer save(List<T> models) {
        return commonMapper.insertList(models);
    }

    @Override
    @Transactional
    public Integer deleteById(Serializable id) {
        return commonMapper.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional
    public Integer deleteByIds(String ids) {
        return commonMapper.deleteByIds(ids);
    }

    @Override
    @Transactional
    public Integer update(T model) {
        return commonMapper.updateByPrimaryKeySelective(model);
    }

    @Override
    public T getById(Serializable id) {
        return commonMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<T> getByField(String fieldName, Object value) throws ServiceException {
        try {
            T model = modelClass.newInstance();
            Field field = modelClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(model, value);
            return commonMapper.select(model);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public List<T> getByIds(String ids) {
        return commonMapper.selectByIds(ids);
    }

    @Override
    public List<T> getByCondition(Condition condition) {
        return commonMapper.selectByCondition(condition);
    }

    @Override
    public List<T> getAll() {
        PageHelper.startPage(0, 0);
        return commonMapper.selectAll();
    }

    @Override
    public PageInfo<T> getPage() {
        PageHelper.startPage(request);
        List<T> list = commonMapper.selectAll();
        return new PageInfo<T>(list);
    }

}
