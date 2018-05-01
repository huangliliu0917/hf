package com.hf.core.dao.local;

import com.hf.core.model.po.UserChannelAccount;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface UserChannelAccountDao {
    int deleteByPrimaryKey(Long id);

    int insert(UserChannelAccount record);

    int insertSelective(UserChannelAccount record);

    UserChannelAccount selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserChannelAccount record);

    int updateByPrimaryKey(UserChannelAccount record);

    UserChannelAccount selectByUnq(@Param("groupId")Long groupId,@Param("channelProvider")String channelProvider);

    int addAmount(@Param("id")Long id, @Param("amount")BigDecimal amount,@Param("version") int version);

    int lockAmount(@Param("id")Long id, @Param("amount")BigDecimal amount,@Param("version") int version);

    int unlock(@Param("id")Long id, @Param("amount")BigDecimal amount,@Param("version") int version);
}