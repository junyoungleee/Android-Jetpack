package com.june.infinite.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeysDao {

    // 네트워크에서 Repos를 가져올 때마다 Remotekey를 생성
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<RemoteKeys>)

    // id를 기준으로 remoteKey 가져옴
    @Query("SELECT * FROM remote_keys WHERE repoId = :repoId")
    suspend fun remoteKeysRepoId(repoId: Long): RemoteKeys?

    // RemoteKeys 지우기 → 새 쿼리가 있을 때마다 사용
    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKeys()
}