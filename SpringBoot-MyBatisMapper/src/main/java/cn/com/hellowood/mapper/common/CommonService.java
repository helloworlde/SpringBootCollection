package cn.com.hellowood.mapper.common;

import cn.com.hellowood.mapper.utils.ServiceException;
import com.github.pagehelper.PageInfo;
import tk.mybatis.mapper.entity.Condition;

import java.io.Serializable;
import java.util.List;

/**
 * The interface Common service.
 *
 * @param <T> the type of model
 */
public interface CommonService<T> {
    /**
     * Save object selective
     *
     * @param model the model
     * @return the integer
     */
    Integer save(T model);

    /**
     * Save objects selective.
     *
     * @param models the model list
     * @return the integer
     */
    Integer save(List<T> models);

    /**
     * Delete object by id.
     *
     * @param id the id
     * @return the integer
     */
    Integer deleteById(Serializable id);

    /**
     * Delete object by ids.
     *
     * @param ids the primary keys
     * @return the integer
     */
    Integer deleteByIds(String ids);

    /**
     * Update object by model.
     *
     * @param model the model
     * @return the integer
     */
    Integer update(T model);

    /**
     * Get object by id.
     *
     * @param id the id
     * @return the object
     */
    T getById(Serializable id);

    /**
     * Get objects by field.
     *
     * @param fieldName the field name
     * @param value     the value
     * @return the objects
     * @throws ServiceException the service exception
     */
    List<T> getByField(String fieldName, Object value) throws ServiceException;

    /**
     * Get objects by primary keys.
     *
     * @param ids the ids
     * @return the objects
     */
    List<T> getByIds(String ids);

    /**
     * Get objects by condition.
     *
     * @param condition the condition
     * @return the objects
     */
    List<T> getByCondition(Condition condition);

    /**
     * Gets all objects.
     *
     * @return the objects
     */
    List<T> getAll();

    PageInfo<T> getPage();
}
