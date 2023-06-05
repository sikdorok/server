package com.ddd.chulsi.infrastructure.config.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Map;
import java.util.stream.Collectors;

public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {

    private CircularList<String> dataSourceNameList;

    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        super.setTargetDataSources(targetDataSources);

        // Datasource 리스트에서 key 이름이 slave 인것을 가져와서 리스트 담음
        dataSourceNameList = new CircularList<>(
            targetDataSources.keySet()
                .stream()
                .filter(key -> key.toString().contains("slave"))
                .map(Object::toString)
                .collect(Collectors.toList())
        );
    }

    @Override
    protected Object determineCurrentLookupKey() {
        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly(); // 트랜잭션 readOnly 속성 가져오는 것으로 추측
        if(isReadOnly) { // readOnly 라면 slave 속성 가져옴
            return dataSourceNameList.getOne() + "_" + ContextHolder.get();
        } else { // 아니라면 마스터
            return "master_" + ContextHolder.get();
        }
    }

}
