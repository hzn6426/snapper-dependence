/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.orm.base;

import com.alibaba.fastjson.JSONObject;
import com.baomibing.cache.CacheService;
import com.baomibing.core.base.MBaseService;
import com.baomibing.core.common.Assert;
import com.baomibing.core.common.CollectionMapperDecorator;
import com.baomibing.core.common.SearchResult;
import com.baomibing.core.exception.ExceptionEnum;
import com.baomibing.core.exception.ServerRuntimeException;
import com.baomibing.core.wrap.OrderByWrap;
import com.baomibing.tool.constant.RedisKeyConstant;
import com.baomibing.tool.util.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.github.dozermapper.core.Mapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.checkerframework.checker.units.qual.N;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

/**
 * 通用基于mybatis-plus的服务方法基类
 * <ul>
 * <li>T是数据库对应的实体对象</li>
 * <li>M是实体对象对应的Mapper</li>
 * <li>V是传过来的DTO对象</li>
 * </ul>
 * 
 * @param <M> 对象对应的Mapper
 * @param <T> 表格实体类
 * @param <V> DTO 类
 * @author zening
 * @since 1.0.0
 */
public abstract class  MBaseServiceImpl<M extends BaseMapper<T>,T, V> extends ContextServiceImpl
		implements MBaseService<V> {
	
	@Autowired protected M baseMapper;
	@Autowired protected CacheService cacheService;
	@Autowired protected Mapper beanMapper;
	@Autowired protected CollectionMapperDecorator collectionMapper;
	
	protected Class<M> mapperClassType;
	protected  Class<T> entityClassType;
	protected Class<V> vmodelClassType;

	protected String reflectForMapperClass() {
		return mapperClassType.getName();
	}

	protected Class<T> reflectForEntityClass() {
		return entityClassType;
	}
	
	@SuppressWarnings("unchecked")
	public  MBaseServiceImpl() {
		//获取泛型参数的Class类型
		Type genType = getClass().getGenericSuperclass();  
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        mapperClassType = (Class<M>) params[0];
        entityClassType = (Class<T>) params[1];
        vmodelClassType = (Class<V>) params[2];
	}

	
	protected int offset(int pageNumber, int pageSize) {
		return PageUtil.offsetCurrent(pageNumber, pageSize);
	}

	protected LambdaQueryWrapper<T> lambdaQuery() {
		return new LambdaQueryWrapper<>();
	}

	protected void doSetTenantId(V v) {
		String tenantId = this.currentTenantId();
		if (Checker.beNotEmpty(tenantId)) {
			if (BeanUtil.hasPrperty(v, "tenantId")) {
				BeanUtil.setProperty(v, "tenantId", tenantId);
			}
		}
	}
	
	protected T mapper2t(V v) {
		if (Checker.beNull(v))
			return null;
		return beanMapper.map(v, entityClassType);
	}
	
	protected V mapper2v(T t) {
		if (Checker.beNull(t))
			return null;
		return beanMapper.map(t, vmodelClassType);
	}
	
	/**
	 * 将 T 类型对象(<code>entity</code>)集合转化成 V 类型对象(<code>dto</code>)集合
	 * @param tlist Dto列表
	 * @return
	 */
	protected List<V> mapper(List<T> tlist) {
		if (Checker.beEmpty(tlist))
			return Lists.newArrayList();
		return new ArrayList<>(collectionMapper.mapCollection(tlist, vmodelClassType));
	}

	protected  List<N> mapper(List<M> mlist, Class<N> clazz) {
		return Lists.newArrayList(collectionMapper.mapCollection(mlist, clazz));
	}

	private T autoFill4Save(V v) {
		//自动填充ID
		if (BeanUtil.hasPrperty(v, "id") && Checker.beEmpty(ObjectUtil.toStringIfNotNull(BeanUtil.getProperty(v, "id")))) {
			BeanUtil.setProperty(v, "id", SnowflakeIdWorker.getId());
		}
		doSetTenantId(v);
		T t = beanMapper.map(v, entityClassType);
		// 如果对象未设置groupId,默认为本人
		if (BeanUtil.hasPrperty(t, "groupId")) {
			Object g = BeanUtil.getProperty(t, "groupId");
			String groupId = g == null ? "" : g.toString();
			if (Checker.beEmpty(groupId)) {
				BeanUtil.setProperty(t, "groupId", currentUserGroupId());
			}
		}
		Date now = new Date();
		if (t instanceof MBaseModel) {
			MBaseModel bm = (MBaseModel) t;
			if (Checker.beNotEmpty(currentUserName())) {
//				if (Checker.beEmpty(bm.getCreateUser())) {
					bm.setCreateUser(currentUserName());
//				}
				bm.setUpdateUser(currentUserName());
			}
			if (Checker.beNotEmpty(currentUserCnName())) {
//				if (Checker.beEmpty(bm.getCreateUserCnName())) {
					bm.setCreateUserCnName(currentUserCnName());
					bm.setUpdateUserCnName(currentUserCnName());
//				}

			}
			bm.setCreateTime(now);
			bm.setUpdateTime(now);
		}

		return t;
	}

	private T autoFill4Update(V v) {
		T t = beanMapper.map(v, entityClassType);
		if (t instanceof MBaseModel) {
			MBaseModel bm = (MBaseModel) t;
			if (Checker.beNotEmpty(currentUserName())) {
				bm.setUpdateUser(currentUserName());
			}
			if (Checker.beNotEmpty(currentUserCnName())) {
				bm.setUpdateUserCnName(currentUserCnName());
			}
			bm.setUpdateTime(new Date());
		}
		return t;
	}

	
	protected SearchResult<V> search(LambdaQueryWrapper<T> wrapper, OrderByWrap orders, int pageNumber, int pageSize) {
		if (Checker.beNull(wrapper)) {
			wrapper = new LambdaQueryWrapper<>();
		}
		Page<T> page = new Page<>(pageNumber,pageSize);
		
		if (Checker.beNotNull(orders)) {
			if (Checker.beNotEmpty(orders.getAscList())) {
				String ascValues = String.join(",", orders.getAscList());
				String sql = MessageFormat.format(" order by {0} asc ", ascValues);
				wrapper.last(sql);//orderByAsc(true, () -> ConvertUtil.toArray(orders.getAscList(), String.class));
			}
			if (Checker.beNotEmpty(orders.getDescList())) {
				String ascValues = String.join(",", orders.getDescList());
				String sql = MessageFormat.format(" order by {0} desc ", ascValues);
				wrapper.last(sql);
			}
		}
		
		IPage<T> pageResult = this.baseMapper.selectPage(page, wrapper);
		if (Checker.beEmpty(pageResult.getRecords())) {
			return new SearchResult<>(0, Lists.newArrayList());
		}
		
		int count = (int) pageResult.getTotal();// this.baseMapper.selectCount(wrapper);
		return new SearchResult<>(count, new ArrayList<>(collectionMapper.mapCollection(pageResult.getRecords(), vmodelClassType)));
	}
	
	protected SearchResult<V> search(LambdaQueryWrapper<T> wrapper, int pageNumber, int pageSize) {
		if (Checker.beNull(wrapper)) {
			wrapper = new LambdaQueryWrapper<>();
		}
		int count = Math.toIntExact(this.baseMapper.selectCount(wrapper));
		if (count == 0) {
			return new SearchResult<V>(0, Lists.newArrayList());
		}

		int offset = PageUtil.offsetCurrent(pageNumber, pageSize);
		String limitOffset = " limit {0} offset {1} ";
		String sql = MessageFormat.format(limitOffset, new Integer(pageSize).toString(), new Integer(offset).toString());
		wrapper.last(sql);

		List<T> list = this.baseMapper.selectList(wrapper);
		List<V> vlist = new ArrayList<>(collectionMapper.mapCollection(list, vmodelClassType));

		return new SearchResult<V>(count, vlist);
	}


	@Override
	public V getIt(Serializable pk) {
		if (Checker.beNull(pk)) {
			return null;
		}
		String value = cacheService.get(RedisKeyConstant.KEY_SERVICE_GET_ID_CACHE_PREFIX + pk.toString());
		if (Checker.beNotEmpty(value)) {
			return JSONObject.parseObject(value, vmodelClassType);
		}
		T t =  this.baseMapper.selectById(pk);
		if (Checker.beNull(t)) {
			return null;
		}
        return beanMapper.map(t, vmodelClassType);
	}

	@Override
	public V getItCahe(Serializable pk) {
		if (Checker.beNull(pk)) {
			return null;
		}
		String value = cacheService.get(RedisKeyConstant.KEY_SERVICE_GET_ID_CACHE_PREFIX + pk.toString());
		if (Checker.beNotEmpty(value)) {
			return JSONObject.parseObject(value, vmodelClassType);
		}
		V v = getIt(pk);
		if (Checker.beNull(v)) {
			return null;
		}
		cacheService.set(RedisKeyConstant.KEY_SERVICE_GET_ID_CACHE_PREFIX + pk, JSONObject.toJSONString(v), 30 * 60);
		return v;
	}

	@Override
	public List<V> gets(Set<? extends Serializable> idSet) {
		List<V> vlist = new ArrayList<>();
		Collection<T> list =  this.baseMapper.selectBatchIds(idSet);
		if (Checker.beEmpty(list)) {
			return vlist;
		}
		vlist = new ArrayList<>(collectionMapper.mapCollection(list, vmodelClassType));
		return vlist;
	}


	protected <E> void assertBeLock(E e) {
		if (Checker.beNull(e)) return;
		if (BeanUtil.hasPrperty(e, "beLock")) {
			Boolean beLock = BeanUtil.getProperty(e, "beLock");
			if (Boolean.TRUE.equals(beLock)) {
				throw new ServerRuntimeException(ExceptionEnum.OBJECT_BE_LOCK_NOT_OPERATE);
			}
		}
	}


	@Override
	public void saveIt(V v) {
		Assert.CheckArgument(v);
		T t = autoFill4Save(v);
		this.baseMapper.insert(t);
//		//进行转化，目的是为了保存后 v中有主键
//		if (BeanUtil.hasPrperty(v, "id")) {
//			BeanUtil.setProperty(v, "id", BeanUtil.getProperty(t, "id"));
//		}
	}



	@Override
	public void updateIt(V v) {
		Assert.CheckArgument(v);
		T t = autoFill4Update(v);
		int num = this.baseMapper.updateById(t);//(t, wrapper);
		Assert.CheckEqual(num, 0, ExceptionEnum.NO_DATA_PERMISSION_FOR_UPDATE);
		Assert.CheckNotEqual(num, 1, ExceptionEnum.INVALID_UPDATE_NUM);
	}

	@Override
	public void saveOrUpdate(V v) {
		Assert.CheckArgument(v);
		Object idValue = BeanUtil.getProperty(v, "id");
		if (Checker.beNull(idValue)) {
			saveIt(v);
		} else {
			updateIt(v);
		}
	}

	private QueryWrapper<T> primaryKeyCondition(V v) {
		Object idValue = BeanUtil.getProperty(v, "id");
		return new QueryWrapper<T>().eq("id", idValue);
	}

	@Override
	public void deleteIt(V v) {
		Assert.CheckArgument(v);
		QueryWrapper<T> wrapper = primaryKeyCondition(v);
		Assert.CheckNotNull(wrapper,ExceptionEnum.INVALID_PK_FOR_DELETE);
		int num = this.baseMapper.delete(wrapper);
		Assert.CheckEqual(num, 0, ExceptionEnum.NO_DATA_PERMISSION_FOR_DELETE);
		Assert.CheckNotEqual(num, 1, ExceptionEnum.INVALID_DELETE_NUM);
	}

	
	@Override
	public void deletes(Set<? extends Serializable> ids) {
		//delete in db
		Assert.CheckArgumentStrict(ids);
		int num = this.baseMapper.deleteBatchIds(ids);
		Assert.CheckNotEqual(num, ids.size(), ExceptionEnum.INVALID_DELETE_NUM);
		
	}

	
	@Override
	public void saveItBatch(List<V> elist) {
		Assert.CheckArgument(elist);
		List<T> tlist = Lists.newArrayList();
		for (V v : elist) {
			tlist.add(autoFill4Save(v));
		}
		executeBatch(tlist, (sqlSession, entity) -> sqlSession.insert(getSqlStatement(SqlMethod.INSERT_ONE), entity));

	}


	@Override
	public void updateItBatch(List<V> elist) {
		Assert.CheckArgument(elist);
		List<T> tlist = Lists.newArrayList();
		for (V v : elist) {
			tlist.add(autoFill4Update(v));
		}
		AtomicInteger rows = new AtomicInteger();
		executeBatch(tlist,  (sqlSession, entity) -> {
			MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
			param.put(Constants.ENTITY, entity);

			int number = sqlSession.update(getSqlStatement(SqlMethod.UPDATE_BY_ID), param);
			rows.addAndGet(number);
		});
//		Assert.CheckNotEqual(rows.get(), elist.size(), ExceptionEnum.INVALID_UPDATE_NUM);
	}

	@Override
	public void saveOrUpdateBatch(List<V> elist) {
		Assert.CheckArgument(elist);
		List<V> saves = Lists.newArrayList();
		List<V> updates = Lists.newArrayList();
		for (V v : elist) {
			Object idValue = BeanUtil.getProperty(v, "id");
			if (Checker.beNull(idValue)) {
				saves.add(v);
			} else {
				updates.add(v);
			}
		}
		if (Checker.beNotEmpty(saves)) {
			saveItBatch(saves);
		}
		if (Checker.beNotEmpty(updates)) {
			updateItBatch(updates);
		}
	}

	@Override
	public void deleteItBatch(List<V> elist) {
		Assert.CheckArgument(elist);
		Set<? extends Serializable> ids = Sets.newHashSet();
		for (V v : elist) {
			if (BeanUtil.hasPrperty(v, "id")) {
				ids.add(BeanUtil.getProperty(v, "id"));
			}
		}
		deletes(ids);
		
	}


	//reference from mybatis-plus ServiceImpl
	private final static int DEFAULT_BATCH_SIZE = 500;
	private final Log log = LogFactory.getLog(getClass());

	private String getSqlStatement(SqlMethod sqlMethod) {
		return SqlHelper.getSqlStatement(mapperClassType, sqlMethod);
	}

	private  <E> void executeBatch(Collection<E> list, int batchSize, BiConsumer<SqlSession, E> consumer) {
		SqlHelper.executeBatch(entityClassType, log, list, batchSize, consumer);
	}

	private  <E> void executeBatch(Collection<E> list, BiConsumer<SqlSession, E> consumer) {
		 executeBatch(list, DEFAULT_BATCH_SIZE, consumer);
	}

}
