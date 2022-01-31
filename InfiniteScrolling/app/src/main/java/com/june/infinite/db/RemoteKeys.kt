package com.june.infinite.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * PagingState에서 마지막으로 로드된 항목을 가져오면 항목이 속한 페이지의 인덱스를 알 수 없음
 * → remote_key라고 하는 각 Repo의 다음/이전 키를 저장하는 다른 테이블 추가
 *
 * prevKey와 nextKey의 경우, 데이터를 추가할 수 없을 땐 null일 수 있음
 */
@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val repoId: Long,
    val prevKey: Int?,
    val nextKey: Int?
)
