package com.hf.core.api;

import com.hf.base.contants.CodeManager;
import com.hf.base.contants.Constants;
import com.hf.base.enums.*;
import com.hf.base.exceptions.BizFailException;
import com.hf.base.model.*;
import com.hf.base.utils.Pagenation;
import com.hf.base.utils.ResponseResult;
import com.hf.base.utils.TypeConverter;
import com.hf.base.utils.Utils;
import com.hf.core.biz.AccountBiz;
import com.hf.core.biz.ChannelBiz;
import com.hf.core.biz.TrdBiz;
import com.hf.core.biz.UserBiz;
import com.hf.core.biz.service.ArchService;
import com.hf.core.biz.service.CacheService;
import com.hf.core.biz.service.UserService;
import com.hf.core.dao.local.*;
import com.hf.core.model.dto.UserGroupDto;
import com.hf.core.model.dto.UserGroupRequest;
import com.hf.core.model.dto.UserInfoDto;
import com.hf.core.model.dto.UserInfoRequest;
import com.hf.core.model.po.*;
import com.hf.core.model.po.Account;
import com.hf.core.model.po.AdminAccount;
import com.hf.core.model.po.AdminBankCard;
import com.hf.core.model.po.Channel;
import com.hf.core.model.po.ChannelProvider;
import com.hf.core.model.po.UserBankCard;
import com.hf.core.model.po.UserChannel;
import com.hf.core.model.po.UserGroup;
import com.hf.core.model.po.UserGroupExt;
import com.hf.core.model.po.UserInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/user")
public class JhUserApi {
    @Autowired
    private UserBiz userBiz;
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private UserBankCardDao userBankCardDao;
    @Autowired
    private UserChannelDao userChannelDao;
    @Autowired
    private ChannelBiz channelBiz;
    @Autowired
    private AdminBankCardDao adminBankCardDao;
    @Autowired
    private TrdBiz trdBiz;
    @Autowired
    private AccountBiz accountBiz;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private AdminAccountDao adminAccountDao;
    @Autowired
    private ChannelProviderDao channelProviderDao;
    @Autowired
    private UserGroupExtDao userGroupExtDao;
    @Autowired
    private ArchService archService;
    @Autowired
    private AccountOprLogDao accountOprLogDao;
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/get_user_list",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<List<UserInfoDto>> getUserList(@RequestBody UserInfoRequest request) {
        List<UserInfoDto> list = userBiz.getUserList(request);
        return ResponseResult.success(list);
    }

    @RequestMapping(value = "/get_user_group_list",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<Pagenation<UserGroup>> getUserGroupList(@RequestBody UserGroupRequest request) {
        if(request.getStatus()!= null) {
            request.setNotStatus(GroupStatus.CANCEL.getValue());
        }
        List<UserGroup> list = userBiz.getUserGroupList(request);
        int currentPage = null == request.getPageIndex()?1:request.getPageIndex();
        int pageSize = null==request.getPageSize()? Constants.pageSize:request.getPageSize();
        Pagenation<UserGroup> pagenation = new Pagenation<UserGroup>(list,currentPage,pageSize);
        return ResponseResult.success(pagenation);
    }

    @RequestMapping(value = "/get_user_info_by_id",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseResult<UserInfo> getUserInfoById(@RequestBody Map<String,Object> data) {
        Long id = new BigDecimal(data.get("id").toString()).longValue();
        UserInfo userInfo = userInfoDao.selectByPrimaryKey(id);
        return ResponseResult.success(userInfo);
    }

    @RequestMapping(value = "/get_user_info",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseResult<UserInfo> getUserInfo(@RequestBody Map<String,Object> params) {
        if(MapUtils.isEmpty(params) || Objects.isNull(params.get("loginId")) || Objects.isNull(params.get("password")) || Objects.isNull(params.get("userType"))) {
            return ResponseResult.failed(CodeManager.PARAM_INVALID,"param invalid",new UserInfo());
        }

        String loginId = String.valueOf(params.get("loginId"));
        String password = String.valueOf(params.get("password"));
        int userType = new BigDecimal(params.get("userType").toString()).intValue();

        UserInfo userInfo = userInfoDao.selectByLoginId(loginId, Utils.convertPassword(password));

        if(Objects.isNull(userInfo)) {
            return ResponseResult.failed(CodeManager.GET_USER_FAILED,"用户不存在",new UserInfo());
        }

        UserGroup userGroup = userGroupDao.selectByPrimaryKey(userInfo.getGroupId());

        if(userGroup.getType() != userType) {
            if(userType==3 && userGroup.getType() == 10) {
                return ResponseResult.success(userInfo);
            }
            if(userType ==2 && userGroup.getType() == GroupType.SALES.getValue()) {
                return ResponseResult.success(userInfo);
            }
            return ResponseResult.failed(CodeManager.GET_USER_FAILED,"用户类型错误",new UserInfo());
        }

        return ResponseResult.success(userInfo);
    }

    @RequestMapping(value = "/get_user_group_by_id",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseResult<UserGroup> getUserGroupById(@RequestBody Map<String,Object> data) {
        Long groupId = new BigDecimal(data.get("id").toString()).longValue();
        UserGroup userGroup = userGroupDao.selectByPrimaryKey(groupId);
        return ResponseResult.success(userGroup);
    }

    @RequestMapping(value = "/edit_password",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseResult<Boolean> editPassword(@RequestBody Map<String,Object> params) {
        Map<String,Object> map = Utils.buildMap("userId",params.get("userId"),
                "ypassword",Utils.convertPassword(String.valueOf(params.get("ypassword"))),
                "newpassword",Utils.convertPassword(String.valueOf(params.get("newpassword"))),
                "newpasswordok",Utils.convertPassword(String.valueOf(params.get("newpasswordok"))));

        int count = userInfoDao.resetPassword(map);

        if(count == 1) {
            return ResponseResult.success(Boolean.TRUE);
        }

        return ResponseResult.failed(CodeManager.RESET_PASSWORD_FAILED,"update password failed",Boolean.FALSE);
    }

    @RequestMapping(value = "/get_channel_list",method = RequestMethod.GET ,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseResult<List<Channel>> getChannelList() {
        List<Channel> list = channelDao.selectForList();
        return ResponseResult.success(list);
    }

    @RequestMapping(value = "/get_ava_channel_list",method = RequestMethod.GET ,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseResult<List<Channel>> getAvaChannelList() {
        List<Channel> list = channelDao.selectForAvaList();
        return ResponseResult.success(list);
    }

    @RequestMapping(value = "/add_channel",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public ResponseResult<Boolean> addChannel(@RequestBody Channel channel) {
        try {
            channelDao.insertSelective(channel);
        } catch (Exception e) {
            return ResponseResult.success(Boolean.FALSE);
        }
        return ResponseResult.success(Boolean.TRUE);
    }

    @RequestMapping(value = "/customer_register",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseResult<String> customerRegister(@RequestBody HashMap<String,String> datas) {
        String loginId = datas.get("loginId");
        String password = datas.get("password");
        String inviteCode = datas.get("inviteCode");
        String subGroupId = datas.get("subGroupId");

        if(Utils.isEmpty(loginId) || Utils.isEmpty(password)) {
            return ResponseResult.failed(CodeManager.PARAM_INVALID,"param invalid","FAILED");
        }

        try {
            userBiz.register(loginId,password,inviteCode,subGroupId);
        } catch (Exception e) {
            return ResponseResult.failed(CodeManager.BIZ_FAIELD,e.getMessage(),"FAIELD");
        }

        return ResponseResult.success("SUCCESS");
    }

    @RequestMapping(value = "/edit_user_info",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<UserInfo> editUserInfo(@RequestBody Map<String,Object> data) {
        try {
            UserInfo userInfo = TypeConverter.convert(data,UserInfo.class);
            userBiz.edit(userInfo);

            userInfo = userInfoDao.selectByPrimaryKey(userInfo.getId());
            return ResponseResult.success(userInfo);

        } catch (Exception e) {
            Long id = Long.parseLong(data.get("id").toString());
            UserInfo userInfo = userInfoDao.selectByPrimaryKey(id);
            return ResponseResult.failed(CodeManager.BIZ_FAIELD,e.getMessage(),userInfo);
        }
    }

    @RequestMapping(value = "/edit_user_group",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<UserGroup> editUserGroup(@RequestBody Map<String,Object> data) {
        try {
            UserGroup userGroup = TypeConverter.convert(data, UserGroup.class);
            userBiz.edit(userGroup);

            userGroup = userGroupDao.selectByPrimaryKey(userGroup.getId());

            return ResponseResult.success(userGroup);
        } catch (Exception e) {
            Long id = Long.parseLong(String.valueOf(data.get("id")));
            UserGroup userGroup = userGroupDao.selectByPrimaryKey(id);
            return ResponseResult.failed(CodeManager.BIZ_FAIELD,e.getMessage(),userGroup);
        }
    }

    @RequestMapping(value = "/get_user_bank_card",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<List<UserBankCard>> getUserBankCards(@RequestBody Map<String,String> params) {
        Long groupId = new BigDecimal(params.get("groupId")).longValue();
        List<UserBankCard> list = userBankCardDao.selectByUser(groupId);
        return ResponseResult.success(list);
    }

    @RequestMapping(value = "/get_user_bank_card_detail",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<UserBankCard> getUserBankCardDetail(@RequestBody Map<String,String> params) {
        if(Objects.isNull(params.get("id"))) {
            return ResponseResult.success(new UserBankCard());
        }
        Long id = Long.parseLong(params.get("id"));
        UserBankCard userBankCard = userBankCardDao.selectByPrimaryKey(id);
        return ResponseResult.success(userBankCard);
    }

    @RequestMapping(value = "/save_user_card",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<Boolean> saveBankCard(@RequestBody Map<String,Object> data) {
        UserBankCard userBankCard ;
        try {
            userBankCard = TypeConverter.convert(data, UserBankCard.class);
            userBiz.saveBankCard(userBankCard);
        }catch (Exception e) {
            throw new BizFailException("解析参数失败");
        }
        return ResponseResult.success(Boolean.TRUE);
    }

    @RequestMapping(value = "/submit_user_to_admin",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<Boolean> submitUserToAdmin(@RequestBody Map<String,String> ids) {
        Long userId = new BigDecimal(ids.get("userId")).longValue();
        Long groupId = new BigDecimal(ids.get("groupId")).longValue();

        if(Objects.isNull(userId) || Objects.isNull(groupId)) {
            throw new BizFailException("userId or groupId cannot be null");
        }

        userBiz.submit(userId,groupId);

        return ResponseResult.success(Boolean.TRUE);
    }

    @RequestMapping(value = "/get_user_auth_status",method = RequestMethod.GET)
    public String getUserAuthStatus (Long userId) {
        UserInfo userInfo = userInfoDao.selectByPrimaryKey(userId);
        switch (userInfo.getStatus()) {
            case 0:
                return "请补充用户信息";
            case 1:
                return "审核中";
            case 2:
                return "您已成功认证!";
            case 3:
                return "请求已被驳回";
            default:
                return "您已成功认证!";
        }
    }

    @RequestMapping(value = "/get_user_list_by_groupId",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<List<UserInfo>> getUserListByGroupId(@RequestBody Map<String,Object> params) {
        Long groupId = Long.parseLong(params.get("groupId").toString());
        List<UserInfo> list = userInfoDao.selectByGroupId(groupId);
        return ResponseResult.success(list);
    }



    @RequestMapping(value = "/get_user_channel_list",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<List<UserChannel>> getUserChannelList(@RequestBody Map<String,String> params) {
        Long groupId = new BigDecimal(params.get("groupId")).longValue();
        List<UserChannel> userChannels = userChannelDao.selectByGroupId(groupId);
        return ResponseResult.success(userChannels);
    }

    @RequestMapping(value = "/get_user_channel_by_id",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<UserChannel> getUserChannelById(@RequestBody Map<String,String> params) {
        Long id = new BigDecimal(params.get("id")).longValue();
        UserChannel userChannel = userChannelDao.selectByPrimaryKey(id);
        return ResponseResult.success(userChannel);
    }

    @RequestMapping(value = "/save_channel",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<Boolean> saveChannel(@RequestBody Map<String,Object> data) {
        try {
            Channel channel = TypeConverter.convert(data, Channel.class);
            if(channel.getId() != null && channel.getId() > 0) {
                channelDao.updateByPrimaryKeySelective(channel);
            } else {
                ChannelProvider channelProvider = channelProviderDao.selectByCode(channel.getProviderCode());
                channel.setProviderName(channelProvider.getProviderName());
                channel.setProviderNo("");
                channelDao.insertSelective(channel);
            }
            return ResponseResult.success(true);
        } catch (Exception e) {
            throw new BizFailException(e.getMessage());
        }
    }

    @RequestMapping(value = "/save_user_channel",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<Boolean> saveUserChannel(@RequestBody Map<String,Object> data) {
        try {
            UserChannel userChannel = TypeConverter.convert(data,UserChannel.class);
            channelBiz.saveUserChannel(userChannel);
            return ResponseResult.success(Boolean.TRUE);
        } catch (BizFailException e) {
            return ResponseResult.failed(CodeManager.BIZ_FAIELD,e.getMessage(),Boolean.FALSE);
        } catch (Exception e) {
            throw new BizFailException(e.getMessage());
        }
    }

    @RequestMapping(value = "/update_user_channel_status",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<Boolean> updateUserChannelStatus(@RequestBody Map<String,Object> data) {
        try {
            UserChannel userChannel = TypeConverter.convert(data,UserChannel.class);
            userChannelDao.updateByPrimaryKeySelective(userChannel);
            return ResponseResult.success(Boolean.TRUE);
        } catch (BizFailException e) {
            return ResponseResult.failed(CodeManager.BIZ_FAIELD,e.getMessage(),Boolean.FALSE);
        } catch (Exception e) {
            throw new BizFailException(e.getMessage());
        }
    }


    @RequestMapping(value = "/del_channel",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<Boolean> deleteChannel(@RequestBody Map<String,Object> data) {
        Long channelId = new BigDecimal(data.get("channelId").toString()).longValue();
        channelDao.deleteByPrimaryKey(channelId);
        return ResponseResult.success(true);
    }

    @RequestMapping(value = "/get_admin_bank_card_list",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<List<AdminBankCard>> getAdminBankCardList(@RequestBody Map<String,String> data) {
        Long companyId = new BigDecimal(data.get("companyId")).longValue();
        String channelNo = String.valueOf(null==data.get("channelNo")?"":data.get("channelNo"));

        List<AdminBankCard> list = adminBankCardDao.select(com.hf.base.utils.MapUtils.buildMap("companyId",companyId,"channelNo",channelNo));
        return ResponseResult.success(list);
    }

    @RequestMapping(value = "/get_admin_bank_card",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<AdminBankCard> getAdminBankCard(@RequestBody Map<String,String> data) {
        Long groupId = null;
        if(!Objects.isNull(data.get("groupId"))) {
            groupId = new BigDecimal(data.get("groupId")).longValue();
        }

        Long companyId = new BigDecimal(data.get("companyId")).longValue();
        AdminBankCard adminBankCard = adminBankCardDao.selectByCompanyId(companyId,groupId);
        return ResponseResult.success(adminBankCard);
    }

    @RequestMapping(value = "/get_admin_bank_card_by_id",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<AdminBankCard> getAdminBankCardById(@RequestBody Map<String,String> data) {
        Long id = new BigDecimal(data.get("id")).longValue();

        AdminBankCard adminBankCard = adminBankCardDao.selectByPrimaryKey(id);
        return ResponseResult.success(adminBankCard);
    }

    @RequestMapping(value = "/save_admin_bank_card",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<Boolean> saveAdminBankCard(@RequestBody Map<String,Object> data) {
        try {
            AdminBankCard adminBankCard = TypeConverter.convert(data,AdminBankCard.class);
            userBiz.saveAdminBankCard(adminBankCard);
            return ResponseResult.success(Boolean.TRUE);
        } catch (Exception e) {
            throw new BizFailException(e.getMessage());
        }
    }

    @RequestMapping(value = "/user_turn_back",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<Boolean> userTurnBack(@RequestBody Map<String,Object> data) {
        Long groupId = Long.parseLong(data.get("id").toString());
        userBiz.userTurnBack(groupId,String.format("%s,%s turn back",data.get("groupId"),data.get("userId")));
        return ResponseResult.success(Boolean.TRUE);
    }

    @RequestMapping(value = "/user_pass",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<Boolean> userPass(@RequestBody Map<String,String> data) {
        Long groupId = new BigDecimal(data.get("id")).longValue();
        try {
            userBiz.userPass(groupId);
            return ResponseResult.success(Boolean.TRUE);
        } catch (BizFailException e) {
            return ResponseResult.failed(CodeManager.BIZ_FAIELD,e.getMessage(),Boolean.FALSE);
        }

    }

    @RequestMapping(value = "/save_user_info",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseResult<Boolean> saveUserInfo(@RequestBody Map<String,String> params) {
        String loginId = params.get("loginId");
        String password = params.get("password");
        String name = params.get("name");
        String idCard = params.get("idCard");
        String tel = params.get("tel");
        String qq = params.get("qq");
        String address = params.get("address");
        String groupId = params.get("groupId");
        String id = params.get("id");
        String type = params.get("type");

        UserInfo userInfo = new UserInfo();
        userInfo.setLoginId(loginId);
        userInfo.setPassword(Utils.convertPassword(password));
        userInfo.setName(name);
        userInfo.setIdCard(idCard);
        userInfo.setTel(tel);
        userInfo.setQq(qq);
        userInfo.setAddress(address);
        if(StringUtils.isNotEmpty(groupId)) {
            userInfo.setGroupId(Long.parseLong(groupId));
        }
        if(StringUtils.isNotEmpty(id)) {
            userInfo.setId(Long.parseLong(id));
        }
        userInfo.setType(Integer.parseInt(type));
        userInfo.setStatus(UserStatus.AVAILABLE.getValue());

        if(StringUtils.isEmpty(id)) {
            userInfo.setInviteCode(RandomStringUtils.random(16, 20, 110, true, true));
            userInfoDao.insertSelective(userInfo);
        } else {
            int count = userInfoDao.updateByPrimaryKeySelective(userInfo);
            if(count<=0) {
                throw new BizFailException(String.format("update user info failed,%s",id));
            }
        }
        return ResponseResult.success(Boolean.TRUE);
    }

    @RequestMapping(value = "/get_sub_user_group",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseResult<List<Map<String,Object>>> getSubUserGroup(@RequestBody Map<String,String> params) {
        List<UserGroup> list = userGroupDao.select1(com.hf.base.utils.MapUtils.buildMap("exceptGroupId",params.get("groupId"),"status", GroupStatus.AVAILABLE.getValue()));
        List<Map<String,Object>> result = new ArrayList<>();
        list.stream().forEach(userGroup -> {
            result.add(com.hf.base.utils.MapUtils.buildMap("id",String.valueOf(userGroup.getId()),"name",userGroup.getName()));
        });
        return ResponseResult.success(result);
    }

    @RequestMapping(value = "/save_user_group",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseResult<Boolean> saveUserGroup(@RequestBody Map<String,Object> params) {
        try {
            UserGroup userGroup = TypeConverter.convert(params,UserGroup.class);
            userBiz.saveUserGroup(userGroup);
        } catch (Exception e) {
            return ResponseResult.failed(CodeManager.BIZ_FAIELD, e.getMessage(),Boolean.FALSE);
        }

        return ResponseResult.success(Boolean.TRUE);
    }

    @RequestMapping(value = "/agent_add_user",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseResult<Boolean> agentAddUser(@RequestBody Map<String,String> params) {
        userBiz.register(params);
        return ResponseResult.success(Boolean.TRUE);
    }

    @RequestMapping(value = "/get_channel_by_id",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseResult<Channel> getChannelById(@RequestBody Map<String,String> params) {
        Long id = new BigDecimal(params.get("id")).longValue();
        Channel channel = channelDao.selectByPrimaryKey(id);
        return ResponseResult.success(channel);
    }

    @RequestMapping(value = "/get_trade_request_list",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseResult<Pagenation<TradeRequestDto>> getTradeRequestList(@RequestBody Map<String,Object> params) {
        try {
            TradeRequest tradeRequest = TypeConverter.convert(params, TradeRequest.class);
            Pagenation<TradeRequestDto> page = trdBiz.getTradeList(tradeRequest);
            return ResponseResult.success(page);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failed(CodeManager.BIZ_FAIELD,e.getMessage(),new Pagenation<TradeRequestDto>(Collections.EMPTY_LIST));
        }
    }

    @RequestMapping(value = "/get_trade_statistics_request_list",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseResult<Pagenation<TradeStatisticsRequestDto>> getTradeStatisticsRequestList(@RequestBody Map<String,Object> params) {
        try {
            TradeStatisticsRequest tradeRequest = TypeConverter.convert(params, TradeStatisticsRequest.class);
            Pagenation<TradeStatisticsRequestDto> page = trdBiz.getTradeStatisticsList(tradeRequest);
            return ResponseResult.success(page);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failed(CodeManager.BIZ_FAIELD,e.getMessage(),new Pagenation<TradeStatisticsRequestDto>(Collections.EMPTY_LIST));
        }
    }

    @RequestMapping(value = "/get_user_trade_statistics_request_list",method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseResult<List<UserStatistic>> getUserTradeStatisticsRequestList(@RequestBody Map<String,Object> params) {
        try {
            TradeStatisticsRequest tradeRequest = TypeConverter.convert(params, TradeStatisticsRequest.class);
            List<UserStatistic> list = trdBiz.getUserStatistics(tradeRequest);
            return ResponseResult.success(list);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failed(CodeManager.BIZ_FAIELD,e.getMessage(),Collections.EMPTY_LIST);
        }
    }

    @RequestMapping(value = "/get_account_list",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseResult<Pagenation<AccountPageInfo>> getAccountList(@RequestBody Map<String,Object> params) {
        try {
            AccountRequest accountRequest = TypeConverter.convert(params, AccountRequest.class);
            Pagenation<AccountPageInfo> page = accountBiz.getAccountPage(accountRequest);
            return ResponseResult.success(page);
        }catch (Exception e) {
            return ResponseResult.failed(CodeManager.BIZ_FAIELD,e.getMessage(),new Pagenation<AccountPageInfo>(Collections.EMPTY_LIST));
        }
    }

    @RequestMapping(value = "/get_admin_account_list",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseResult<Pagenation<AdminAccountPageInfo>> getAdminAccountList(@RequestBody Map<String,Object> params) {
        try {
            AccountRequest accountRequest = TypeConverter.convert(params, AccountRequest.class);
            Pagenation<AdminAccountPageInfo> page = accountBiz.getAdminAccountPage(accountRequest);
            return ResponseResult.success(page);
        }catch (Exception e) {
            return ResponseResult.failed(CodeManager.BIZ_FAIELD,e.getMessage(),new Pagenation<AdminAccountPageInfo>(Collections.EMPTY_LIST));
        }
    }

    @RequestMapping(value = "/get_account_opr_log_list",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseResult<Pagenation<AccountOprInfo>> getAccountOprLogList(@RequestBody Map<String,Object> params) {
        try {
            AccountOprRequest accountOprRequest = TypeConverter.convert(params, AccountOprRequest.class);
            Pagenation<AccountOprInfo> pagenation = accountBiz.getAccountOprPage(accountOprRequest);
            return ResponseResult.success(pagenation);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failed(CodeManager.BIZ_FAIELD, e.getMessage(), new Pagenation<AccountOprInfo>(Collections.EMPTY_LIST));
        }
    }

    @RequestMapping(value = "/get_sum_lock_amount",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseResult<BigDecimal> getLockedAmount(@RequestBody Map<String,Object> params) {
        Long groupId = new BigDecimal(params.get("groupId").toString()).longValue();
        BigDecimal logAmount = accountBiz.getLockedAmount(groupId);
        return ResponseResult.success(logAmount);
    }

    @RequestMapping(value = "/get_account_by_group_id",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseResult<Account> getAccountByGroupId(@RequestBody Map<String,String> params) {
        Long groupId = new BigDecimal(params.get("groupId")).longValue();
        Account account = accountDao.selectByGroupId(groupId);
        return ResponseResult.success(account);
    }

    @RequestMapping(value = "/get_withdraw_fee_rate",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseResult<BigDecimal> getWithDrawFeeRate() {
        String feeRate = cacheService.getProp(Constants.SETTLE_FEE_RATE,"5");
        return ResponseResult.success(new BigDecimal(feeRate));
    }

    @RequestMapping(value = "/get_admin_account_by_group_id",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    ResponseResult<AdminAccount> getAdminAccountByGroupId(@RequestBody Map<String,String> params) {
        if(MapUtils.isEmpty(params) || params.get("groupId") == null) {
            return ResponseResult.failed(CodeManager.BIZ_FAIELD,"no param",null);
        }
        Long groupId =  new BigDecimal(params.get("groupId")).longValue();
        AdminAccount adminAccount = adminAccountDao.selectByGroupId(groupId);
        return ResponseResult.success(adminAccount);
    }

    @RequestMapping(value = "/get_user_group_ext_by_code",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<UserGroupExt> getUserGroupExtByCode(@RequestBody Map<String,Object> data) {
        String providerCode = String.valueOf(data.get("providerCode"));
        Long groupId = new BigDecimal(data.get("groupId").toString()).longValue();
        UserGroupExt userGroupExt = userGroupExtDao.selectByUnq(groupId,providerCode);
        return ResponseResult.success(userGroupExt);
    }

    @RequestMapping(value = "/get_user_group_ext_by_id",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<UserGroupExt> getUserGroupExtById(@RequestBody Map<String,Object> data) {
        Long id = new BigDecimal(data.get("id").toString()).longValue();
        UserGroupExt userGroupExt = userGroupExtDao.selectByPrimaryKey(id);
        return ResponseResult.success(userGroupExt);
    }


    @RequestMapping(value = "/save_user_group_ext",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<Boolean> saveUserGroupExt(@RequestBody Map<String,Object> data) {
        try {
            UserGroupExt userGroupExt = TypeConverter.convert(data,UserGroupExt.class);
            userService.saveUserGroupExt(userGroupExt);
            return ResponseResult.success(true);
        } catch (Exception e) {
            return ResponseResult.failed(CodeManager.BIZ_FAIELD,e.getMessage(),false);
        }
    }

    @RequestMapping(value = "/get_user_channel_info",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<List<UserChannelPage>> getUserChannelInfo(@RequestBody Map<String,Object> data) {
        Long groupId = new BigDecimal(data.get("groupId").toString()).longValue();
        List<UserChannelPage> list = userBiz.getUserChannelInfo(groupId);
        return ResponseResult.success(list);
    }

    @RequestMapping(value = "/get_provider_channel_list",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<List<Channel>> getProviderChannelList(@RequestBody Map<String,Object> data) {
        String providerCode = data.get("providerCode").toString();
        List<Channel> channels = channelDao.selectByProviderCode(providerCode);
        return ResponseResult.success(channels);
    }

    @RequestMapping(value = "/del_group",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<Boolean> deleteGroup(@RequestBody Map<String,Object> data) {
        Long groupId = new BigDecimal(data.get("groupId").toString()).longValue();
        UserGroup userGroup = userGroupDao.selectByPrimaryKey(groupId);

        if(userGroup.getType() == GroupType.SUPER.getValue()) {
            return ResponseResult.failed("9999999","该商户不能删除",false);
        }

        userGroupDao.updateStatusById(userGroup.getId(),userGroup.getStatus(),101);

        List<UserInfo> userInfos = userInfoDao.selectByGroupId(userGroup.getId());
        for(UserInfo userInfo:userInfos) {
            userInfoDao.deleteByPrimaryKey(userInfo.getId());
        }

        return ResponseResult.success(true);
    }

    @RequestMapping(value = "/get_sales_list",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<List<SalesManDto>> getSalesList(@RequestBody Map<String,Object> data) {
        Long groupId = new BigDecimal(data.get("groupId").toString()).longValue();
        List<SalesManDto> list  = userBiz.getSaleList(groupId);
        return ResponseResult.success(list);
    }

    @RequestMapping(value = "/save_sale_info",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<Boolean> saveSale(@RequestBody Map<String,Object> data) {
        String id = String.valueOf(data.get("id"));
        String name = String.valueOf(data.get("name"));
        String loginId = String.valueOf(data.get("loginId"));
        String idCard = String.valueOf(data.get("idCard"));
        String tel = String.valueOf(data.get("tel"));
        String qq = String.valueOf(data.get("qq"));
        String address = String.valueOf(data.get("address"));
        String subGroupId = String.valueOf(data.get("subGroupId"));
        String password = String.valueOf(data.get("password"));

        UserGroup subUserGroup = userGroupDao.selectByPrimaryKey(Long.parseLong(subGroupId));

        UserGroup userGroup = new UserGroup();
        String groupNo = archService.getId();
        userGroup.setGroupNo(groupNo);
        userGroup.setSubGroupNo(subUserGroup.getGroupNo());
        userGroup.setSubGroupName(subUserGroup.getName());
        userGroup.setSubGroupId(subUserGroup.getId());
        userGroup.setCipherCode("");
        userGroup.setTel(tel);
        userGroup.setIdCard(idCard);
        userGroup.setName(name);
        userGroup.setOwnerName(name);
        userGroup.setAddress(address);
        userGroup.setCompanyId(subUserGroup.getCompanyId());
        userGroup.setType(GroupType.SALES.getValue());
        userGroup.setCallbackUrl("");
        userGroup.setStatus(GroupStatus.AVAILABLE.getValue());

        if(StringUtils.isEmpty(id)) {
            userGroupDao.insertSelective(userGroup);

            UserInfo userInfo = new UserInfo();
            userInfo.setIdCard(idCard);
            userInfo.setAddress(address);
            userInfo.setTel(tel);
            userInfo.setQq(qq);
            userInfo.setName(name);
            userInfo.setType(UserType.ADMIN.getValue());
            userInfo.setStatus(UserStatus.AVAILABLE.getValue());
            userInfo.setPassword(Utils.convertPassword(password));
            userInfo.setLoginId(loginId);
            userInfo.setGroupId(userGroup.getId());
            userInfo.setInviteCode(RandomStringUtils.random(16, 20, 110, true, true));

            userInfoDao.insertSelective(userInfo);
        } else {
            userGroup.setId(Long.parseLong(id));
            userGroupDao.updateByPrimaryKeySelective(userGroup);

            List<UserInfo> userInfos = userInfoDao.selectByGroupId(Long.parseLong(id));

            if(CollectionUtils.isEmpty(userInfos)) {
                UserInfo userInfo = new UserInfo();
                userInfo.setIdCard(idCard);
                userInfo.setAddress(address);
                userInfo.setTel(tel);
                userInfo.setQq(qq);
                userInfo.setName(name);
                userInfo.setType(UserType.ADMIN.getValue());
                userInfo.setStatus(UserStatus.AVAILABLE.getValue());
                userInfo.setPassword(Utils.convertPassword(password));
                userInfo.setLoginId(loginId);
                userInfo.setGroupId(userGroup.getId());
                userInfo.setInviteCode(RandomStringUtils.random(16, 20, 110, true, true));

                userInfoDao.insertSelective(userInfo);
            } else {
                UserInfo userInfo = userInfos.get(0);
                userInfo.setIdCard(idCard);
                userInfo.setAddress(address);
                userInfo.setTel(tel);
                userInfo.setQq(qq);
                userInfo.setName(name);
                userInfo.setType(UserType.ADMIN.getValue());
                userInfo.setStatus(UserStatus.AVAILABLE.getValue());
                userInfo.setPassword(Utils.convertPassword(password));
                userInfo.setLoginId(loginId);
                userInfo.setGroupId(userGroup.getId());
                userInfo.setInviteCode(RandomStringUtils.random(16, 20, 110, true, true));
                userInfoDao.updateByPrimaryKeySelective(userInfo);
            }
        }

        return ResponseResult.success(true);
    }

    @RequestMapping(value = "/edit_sub_group",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<Boolean> editSubGroup(@RequestBody Map<String,Object> data) {
        Long groupId = Long.parseLong(data.get("groupId").toString());
        Long subGroupId = Long.parseLong(data.get("subGroupId").toString());
        userBiz.editSubGroup(groupId,subGroupId);
        return ResponseResult.success(true);
    }

    @RequestMapping(value = "/save_cipher_code",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<Boolean> saveCipherCode(@RequestBody Map<String,Object> data) {
        Long groupId = Long.parseLong(data.get("groupId").toString());
        String cipherCode = data.get("cipherCode").toString();
        UserGroup userGroup = new UserGroup();
        userGroup.setId(groupId);
        userGroup.setCipherCode(cipherCode);
        userGroupDao.updateByPrimaryKeySelective(userGroup);
        return ResponseResult.success(true);
    }

    @RequestMapping(value = "/save_callback_url",method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult<Boolean> saveCallBackUrl(@RequestBody Map<String,Object> data) {
        Long groupId = Long.parseLong(data.get("groupId").toString());
        String callBackUrl = data.get("callBackUrl").toString();
        UserGroup userGroup = new UserGroup();
        userGroup.setId(groupId);
        userGroup.setCallbackUrl(callBackUrl);
        userGroupDao.updateByPrimaryKeySelective(userGroup);
        return ResponseResult.success(true);
    }

    @RequestMapping(value = "/get_sum_finish_amount",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseResult<BigDecimal> getSumFinishAmount(@RequestBody Map<String,Object> data) {
        Long groupId = new BigDecimal(data.get("groupId").toString()).longValue();
        BigDecimal finishAmount = accountOprLogDao.sumFinishAmount(Arrays.asList(groupId),Arrays.asList(OprType.PAY.getValue()));
        return ResponseResult.success(finishAmount);
    }

    @RequestMapping(value = "/testApp",method = RequestMethod.POST ,produces = "application/x-www-form-urlencoded")
    public void testApp(HttpServletRequest request, HttpServletResponse response) {
        String memberCode = request.getParameter("memberCode");
        String payMoney = request.getParameter("payMoney");
        String orderNum = request.getParameter("orderNum");
        String callbackUrl = request.getParameter("callbackUrl");
        String goodsName = request.getParameter("goodsName");
        String signStr = request.getParameter("signStr");

        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String name = headers.nextElement();
            String value = request.getHeader(name);
        }
        System.out.println("-------");
    }

    @RequestMapping(value = "/testGet",method = RequestMethod.GET ,produces = "application/x-www-form-urlencoded;charset=UTF-8")
    public void testGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }


        String memberCode = request.getParameter("memberCode");
        String payMoney = request.getParameter("payMoney");
        String orderNum = request.getParameter("orderNum");
        String callbackUrl = request.getParameter("callbackUrl");
        String goodsName = request.getParameter("goodsName");
        String signStr = request.getParameter("signStr");

        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String name = headers.nextElement();
            String value = request.getHeader(name);
        }
        System.out.println("-------");
    }

    @RequestMapping(value = "/check_login_valid",method = RequestMethod.POST)
    public @ResponseBody ResponseResult<Boolean> checkLoginCanUse(@RequestBody Map<String,String> params) {
        String loginId = params.get("loginId");
        UserInfo userInfo = userInfoDao.selectByLoginIdOnly(loginId);
        if (Objects.isNull(userInfo)) {
            return ResponseResult.success(true);
        }
        return ResponseResult.success(false);
    }

    @RequestMapping(value = "/get_order_cal",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseResult<Map<String,BigDecimal>> getOrderCal(@RequestBody Map<String,Object> params) {
        try {
            TradeRequest tradeRequest = TypeConverter.convert(params, TradeRequest.class);
            Map<String,BigDecimal> map = trdBiz.getOrderCal(tradeRequest);
            return ResponseResult.success(map);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failed("99999",e.getMessage(),new HashMap<>());
        }
    }

    @RequestMapping(value = "/get_user_group_of_agent",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody ResponseResult<Pagenation<com.hf.core.model.dto.UserGroupDto>> getUserGroupOfAgent(@RequestBody Map<String,Object> params) {
        try {
            SubGroupRequest subGroupRequest = TypeConverter.convert(params, SubGroupRequest.class);
            Pagenation<UserGroupDto> pagenation = userBiz.getSubUserGroups(subGroupRequest);
            return ResponseResult.success(pagenation);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failed(CodeManager.BIZ_FAIELD,e.getMessage(),null);
        }
    }
}
