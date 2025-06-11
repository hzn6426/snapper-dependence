/**
 * Copyright (c) 2018-2023, zening (316279828@qq.com).
 * <p>
 * Any unauthorised copying, selling, transferring, distributing, transmitting, renting,
 * or modifying of the Software is considered an infringement.
 */
package com.baomibing.core.config;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * 切面事务配置
 * 
 * @author zening
 * @since 1.0.0
 */
@EnableTransactionManagement(order = 0)
@Configuration
public class TransactionConfiguration {

	@Resource private TransactionManager transactionManager;

	@Bean
	public TransactionInterceptor transactionInterceptor() {

		// 只读事物
		RuleBasedTransactionAttribute readOnlyTx = new RuleBasedTransactionAttribute();
		readOnlyTx.setReadOnly(true);
		readOnlyTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		// 持久化事物
		RuleBasedTransactionAttribute requiredTx = new RuleBasedTransactionAttribute();
		readOnlyTx.setReadOnly(false);
		requiredTx.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Throwable.class)));
		requiredTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
		source.addTransactionalMethod("add*", requiredTx);
		source.addTransactionalMethod("save*", requiredTx);
		source.addTransactionalMethod("insert*", requiredTx);
		source.addTransactionalMethod("update*", requiredTx);
		source.addTransactionalMethod("delete*", requiredTx);
		source.addTransactionalMethod("do*", requiredTx);
		source.addTransactionalMethod("generate*", requiredTx);
		source.addTransactionalMethod("list*", readOnlyTx);
		source.addTransactionalMethod("get*", readOnlyTx);
		source.addTransactionalMethod("search*", readOnlyTx);
		source.addTransactionalMethod("map*", readOnlyTx);
		source.addTransactionalMethod("sets*", readOnlyTx);
		return new TransactionInterceptor(transactionManager, source);
	}

	@Bean
    public Advisor txAdviceAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		final String AOP_POINTCUT_EXPRESSION = "execution(* com.baomibing.*.service..*.*(..))";
        pointcut.setExpression(AOP_POINTCUT_EXPRESSION);
		DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, transactionInterceptor());
		advisor.setOrder(10);
		return advisor;
	}

}
