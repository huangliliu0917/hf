package com.hf.admin.api;

import com.hf.admin.model.UserGroupRequest;
import com.hf.admin.model.UserInfoDto;
import com.hf.admin.model.UserInfoRequest;
import com.hf.admin.rpc.AdminClient;
import com.hf.admin.rpc.UserClient;
import com.hf.admin.utils.MapUtils;
import com.hf.base.biz.CacheService;
import com.hf.base.client.DefaultClient;
import com.hf.base.contants.Constants;
import com.hf.base.enums.UserType;
import com.hf.base.enums.WithDrawRole;
import com.hf.base.model.*;
import com.hf.base.utils.Pagenation;
import com.hf.base.utils.ResponseResult;
import com.hf.base.utils.Utils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static com.hf.base.contants.Constants.*;

@Controller
@RequestMapping("/user")
public class UserApi {
    @Autowired
    private CacheService cacheService;
    @Autowired
    private AdminClient adminClient;
    @Autowired
    private DefaultClient client;
    @Autowired
    private UserClient userClient;
    /**
     * 登陆
     * @param loginId
     * @param password
     * @return
     */
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public ModelAndView doLogin(HttpServletRequest request, String loginId, String password) {
        UserInfo userInfo = client.getUserInfo(loginId,password, Constants.GROUP_TYPE_COMPANY);
        ModelAndView modelAndView = new ModelAndView();

        if(Objects.isNull(userInfo) || Objects.isNull(userInfo.getId())) {
            modelAndView.setViewName("redirect:/login.jsp");
            return modelAndView;
        }

        request.getSession().setAttribute(USER_LOGIN_INFO, MapUtils.buildMap("id",userInfo.getId(),"name",userInfo.getName(),"loginId",userInfo.getLoginId(),"userType", UserType.parse(userInfo.getType()).getDesc(),"groupId",userInfo.getGroupId()));
        request.getSession().setAttribute("userId",userInfo.getId());
        request.getSession().setAttribute("groupId",userInfo.getGroupId());
        cacheService.login(userInfo.getId().toString(),request.getSession().getId());

        modelAndView.setViewName("redirect:/common/index");
        return modelAndView;
    }

    @RequestMapping(value = "/logout",method = RequestMethod.GET)
    public ModelAndView logout(HttpServletRequest request) {
        request.getSession().removeAttribute(Constants.USER_LOGIN_INFO);
        request.getSession().removeAttribute("userId");
        request.getSession().removeAttribute("groupId");
        ModelAndView modelAndView = new ModelAndView("redirect:/login.jsp");
        return modelAndView;
    }

    @RequestMapping(value = "/get_user_list",method = RequestMethod.GET)
    public ModelAndView getUserList(HttpServletRequest request) {
        String userId = String.valueOf(request.getSession().getAttribute("userId"));
        UserInfoRequest userInfoRequest = new UserInfoRequest();
        userInfoRequest.setUser(request.getParameter("user"));
        userInfoRequest.setAgent(request.getParameter("agent"));

        if(!StringUtils.isEmpty(request.getParameter("status"))) {
            userInfoRequest.setStatus(Integer.parseInt(request.getParameter("status")));
        }

        if(!StringUtils.isEmpty(request.getParameter("type"))) {
            userInfoRequest.setStatus(Integer.parseInt(request.getParameter("type")));
        }

        userInfoRequest.setAdminId(userId);

        List<UserInfoDto> list = adminClient.getUserListForAdmin(userInfoRequest);

        ModelAndView modelAndView = new ModelAndView("admin_user_index");
        modelAndView.addObject("users",list);
        return modelAndView;
    }

    @RequestMapping(value = "/get_user_group_list",method = RequestMethod.GET)
    public ModelAndView getUserGroupList(HttpServletRequest request) {
        String groupId = String.valueOf(request.getSession().getAttribute("groupId"));
        UserGroupRequest userGroupRequest = new UserGroupRequest();
        userGroupRequest.setUser(request.getParameter("user"));
        userGroupRequest.setAgent(request.getParameter("agent"));

        if(!StringUtils.isEmpty(request.getParameter("status"))) {
            userGroupRequest.setStatus(Integer.parseInt(request.getParameter("status")));
        }

        if(!StringUtils.isEmpty(request.getParameter("type"))) {
            userGroupRequest.setType(Integer.parseInt(request.getParameter("type")));
        }

        userGroupRequest.setCompanyId(groupId);

        Pagenation<UserGroup> pagenation = adminClient.getUserGroupList(userGroupRequest);

        ModelAndView modelAndView = new ModelAndView("admin_user_authorized");
        modelAndView.addObject("pageInfo",pagenation);
        modelAndView.addObject("userGroupRequest",userGroupRequest);
        return modelAndView;
    }

    @RequestMapping(value = "/edit_password",method = RequestMethod.POST)
    public @ResponseBody
    Map<String,Object> editPassword(HttpServletRequest request) {
        String userId = String.valueOf(request.getSession().getAttribute("userId"));
        String ypassword = request.getParameter("ypassword");
        String newpassword = request.getParameter("newpassword");
        String newpasswordok = request.getParameter("newpasswordok");

        boolean result = adminClient.editPassword(userId,ypassword,newpassword,newpasswordok);
        return MapUtils.buildMap("status",result);
    }

    @RequestMapping(value = "/get_channel_info",method = RequestMethod.GET)
    public Map<String,Object> getChannelInfo(Long channelId,Long groupId) {
        return null;
    }

    @RequestMapping(value = "/save_channel",method = RequestMethod.POST)
    public @ResponseBody
    Map<String,Object> saveChannel(HttpServletRequest request) {
        String channelId = request.getParameter("channelId");
        String providerCode = request.getParameter("providerCode");
        String channelName = request.getParameter("channelName");
        String channelCode = request.getParameter("channelCode");
        String channelDesc = request.getParameter("channelDesc");
        String feeRate = request.getParameter("feeRate");
        String url = request.getParameter("url");
        String status = request.getParameter("status");
        String code = request.getParameter("code");
        if(!NumberUtils.isNumber(feeRate)) {
            return MapUtils.buildMap("status",false,"msg","费率不能为空!");
        }

        adminClient.saveChannel(MapUtils.buildMap("providerCode",providerCode,"channelName",channelName,"channelCode",channelCode,"channelDesc",channelDesc,"feeRate",feeRate,"url",url,"status",status,"channelId",channelId,"code",code));

        return MapUtils.buildMap("status",true);
    }

    @RequestMapping(value = "/del_channel",method = RequestMethod.POST)
    public @ResponseBody
    Map<String,Object> delChannel(HttpServletRequest request) {
        String channelId = request.getParameter("channelId");
        boolean result = client.deleteChannel(channelId);
        return MapUtils.buildMap("status",result);
    }

    @RequestMapping(value = "/update_channel_status",method = RequestMethod.POST)
    public @ResponseBody
    Map<String,Object> updateChannelStatus(HttpServletRequest request) {
        String channelId = request.getParameter("pid");
        String isopen = request.getParameter("isopen");
        boolean result = adminClient.saveChannel(MapUtils.buildMap("channelId",channelId,"status",isopen));
        return MapUtils.buildMap("res",result);
    }

    @RequestMapping(value = "/update_user_channel_status",method = RequestMethod.POST)
    public @ResponseBody
    Map<String,Object> updateUserChannelStatus(HttpServletRequest request) {
        String channelId = request.getParameter("id");
        String isopen = request.getParameter("isopen");
        ResponseResult<Boolean> result = adminClient.updateUserChannelStatus(MapUtils.buildMap("id",channelId,"status",isopen));
        return MapUtils.buildMap("res",result.getData(),"msg",result.getMsg());
    }

    @RequestMapping(value = "/save_user_channel",method = RequestMethod.POST)
    public @ResponseBody
    Map<String,Boolean> saveUserChannel(HttpServletRequest request) {
        String groupId = request.getParameter("group");
        String channelId = request.getParameter("channelId");
        String feeRate = request.getParameter("feeRate");
        String id = request.getParameter("id");
        String providerCode = request.getParameter("providerCode");

        ResponseResult<Boolean> result = adminClient.saveUserChannel(MapUtils.buildMap("id",id,"groupId",groupId,"channelId",channelId,"feeRate",feeRate,"providerCode",providerCode));
        return MapUtils.buildMap("status",result.getData(),"msg",result.getMsg());
    }

    @RequestMapping(value = "/get_admin_bank_list",method = RequestMethod.POST)
    public ModelAndView getAdminBankList(HttpServletRequest request) {
        String companyId = request.getSession().getAttribute("groupId").toString();
        String channelNo = request.getParameter("channelNo");
        List<AdminBankCard> list = adminClient.getAdminBankCardList(Long.parseLong(companyId),channelNo);
        list.stream().forEach(adminBankCard -> {
            adminBankCard.setLimitAmount(adminBankCard.getLimitAmount().divide(new BigDecimal("100"),2,BigDecimal.ROUND_HALF_UP));
        });
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin_bank_index");
        modelAndView.addObject("cards",list);
        modelAndView.addObject("channelNo",channelNo);
        return modelAndView;
    }

    @RequestMapping(value = "/save_admin_bank_card",method = RequestMethod.POST)
    public @ResponseBody
    Map<String,Boolean> saveAdminBankCard(HttpServletRequest request) {
        String id = request.getParameter("id");
        String bank = request.getParameter("bank");
        String deposit = request.getParameter("deposit");
        String owner = request.getParameter("owner");
        String bankNo = request.getParameter("bankNo");
        String province = request.getParameter("province");
        String city = request.getParameter("city");
        String companyId = request.getSession().getAttribute("groupId").toString();
        String groupId = "0";
        String mchId = request.getParameter("mchId");
        String outletNo = request.getParameter("outletNo");
        String name = request.getParameter("name");
        String ownerName = request.getParameter("ownerName");
        String limitAmount = request.getParameter("limitAmount");
        String cipherCode = request.getParameter("cipherCode");
        String channelNo = request.getParameter("channelNo");

        boolean status = adminClient.saveAdminBankCard(MapUtils.buildMap("id",id,"bank",bank,"deposit",deposit,"owner",owner,
                "bankNo",bankNo,"province",province,"city",city,"companyId",companyId,"groupId",groupId,
                "mchId",mchId,
                "outletNo",outletNo,
                "name",name,
                "ownerName",ownerName,
                "limitAmount",new BigDecimal(limitAmount).multiply(new BigDecimal(100)),
                "cipherCode",cipherCode,
                "channelNo",channelNo));

        return MapUtils.buildMap("status",status);
    }

    @RequestMapping(value = "/user_member_back",method = RequestMethod.POST)
    public @ResponseBody
    Map<String,Boolean> memberBack(HttpServletRequest request) {
        String id = request.getParameter("uid");
        String groupId = request.getSession().getAttribute("groupId").toString();
        String userId = request.getSession().getAttribute("userId").toString();

        boolean result = adminClient.userTurnBack(MapUtils.buildMap("id",id,"groupId",groupId,"userId",userId));
        return MapUtils.buildMap("status",result);
    }

    @RequestMapping(value = "/user_member_pass",method = RequestMethod.POST)
    public @ResponseBody
    Map<String,Boolean> memberPass(HttpServletRequest request) {
        String id = request.getParameter("uid");
        ResponseResult<Boolean> response = adminClient.memberPass(id);
        if(response.isSuccess()) {
            return MapUtils.buildMap("status",true);
        } else {
            return MapUtils.buildMap("status",false,"msg",response.getMsg());
        }
    }

    @RequestMapping(value = "/user_member_delete",method = RequestMethod.POST)
    public @ResponseBody
    Map<String,Boolean> memberDelete(HttpServletRequest request) {
        String id = request.getParameter("uid");
        ResponseResult<Boolean> response = adminClient.memberDelete(id);
        if(response.isSuccess()) {
            return MapUtils.buildMap("status",true);
        } else {
            return MapUtils.buildMap("status",false,"msg",response.getMsg());
        }
    }

    @RequestMapping(value = "/save_bank_card",method = RequestMethod.POST)
    public @ResponseBody
    Map<String,Object> saveBankCard(HttpServletRequest request, HttpServletResponse response) {
        String groupId = request.getParameter("groupId");
        String id = request.getParameter("id");
        String bank = request.getParameter("bank");
        String bankNo = request.getParameter("bankNo");
        String deposit = request.getParameter("deposit");
        String owner = request.getParameter("owner");
        String province = request.getParameter("province");
        String city = request.getParameter("city");

        ResponseResult<Boolean> responseResult = userClient.saveBankCard(
                MapUtils.buildMap("groupId",groupId,
                        "bank",bank,
                        "bankNo",bankNo,
                        "deposit",deposit,
                        "owner",owner,
                        "province",province,
                        "city",city,
                        "id",id));

        if(responseResult.isSuccess()) {
            return MapUtils.buildMap("status",true);
        }
        return MapUtils.buildMap("status",false);
    }

    @RequestMapping(value = "/save_user_info",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    Map<String,Object> saveUserInfo(HttpServletRequest request, HttpServletResponse response) {
        String loginId = request.getParameter("loginId");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String idCard = request.getParameter("idCard");
        String tel = request.getParameter("tel");
        String qq = request.getParameter("qq");
        String address = request.getParameter("address");
        String id = request.getParameter("id");
        String groupId = request.getParameter("group");
        String type = request.getParameter("type");

        Map<String,Object> map = Utils.buildMap("loginId",loginId,
                "password",password,
                "name",name,
                "idCard",idCard,
                "tel",tel,
                "qq",qq,
                "address",address,
                "groupId",groupId,
                "id",id,
                "type",type);

        boolean result = adminClient.saveUserInfo(map);

        return MapUtils.buildMap("status",result);
    }

    @RequestMapping(value = "/save_user_group",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    Map<String,Object> saveUserGroup(HttpServletRequest request) {
        String id = request.getParameter("id");
        String name = request.getParameter("name");
        String ownerName = request.getParameter("ownerName");
        String idCard = request.getParameter("idCard");
        String tel = request.getParameter("tel");
        String address = request.getParameter("address");
        String type = request.getParameter("type");
        String subGroupId = request.getParameter("subGroupId");
        String cipherCode = request.getParameter("cipherCode");
        String callbackUrl = request.getParameter("callbackUrl");

        Map<String,Object> map = MapUtils.buildMap("groupId",id,
                "name",name,
                "ownerName",ownerName,
                "idCard",idCard,
                "tel",tel,
                "address",address,
                "type",type,
                "subGroupId",subGroupId,
                "cipherCode",cipherCode,
                "callbackUrl",callbackUrl);
        try {
            adminClient.saveUserGroup(map);
            return MapUtils.buildMap("status",true);
        } catch (Exception e) {
            return MapUtils.buildMap("status",false);
        }
    }

    @RequestMapping(value = "/getTrdOrderList",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public ModelAndView getTradeOrderList(HttpServletRequest request) {
        Long groupId = Long.parseLong(request.getSession().getAttribute("groupId").toString());
        int currentPage = 1;
        TradeRequest tradeRequest = new TradeRequest();
        tradeRequest.setCurrentPage(1);
        tradeRequest.setPageSize(15);
        tradeRequest.setGroupId(groupId);
        tradeRequest.setCurrentPage(currentPage);

        if(!StringUtils.isEmpty(request.getParameter("mchId"))) {
            tradeRequest.setMchId(request.getParameter("mchId"));
        }

        if(!StringUtils.isEmpty(request.getParameter("orderid"))) {
            tradeRequest.setOutTradeNo(request.getParameter("orderid"));
        }

        if(!StringUtils.isEmpty(request.getParameter("type"))) {
            tradeRequest.setType(Integer.parseInt(request.getParameter("type")));
        }

        if(!StringUtils.isEmpty(request.getParameter("channelCode"))) {
            tradeRequest.setChannelCode(request.getParameter("channelCode"));
        }

        if(!StringUtils.isEmpty(request.getParameter("status"))) {
            tradeRequest.setStatus(Integer.parseInt(request.getParameter("status")));
        }

        Pagenation<TradeRequestDto> pagenation = client.getTradeList(tradeRequest);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin_order_index");
        modelAndView.addObject("pageInfo",pagenation);
        modelAndView.addObject("requestInfo",tradeRequest);

        List<Channel> channels = adminClient.getChannelList();
        modelAndView.addObject("channels",channels);
        modelAndView.addObject("urlParams",String.format("mchId=%s&orderid=%s&channelCode=%s&status=%s&type=%s",
                StringUtils.isEmpty(tradeRequest.getMchId())?"":tradeRequest.getMchId(),
                StringUtils.isEmpty(tradeRequest.getOutTradeNo())?"":tradeRequest.getOutTradeNo(),
                StringUtils.isEmpty(tradeRequest.getChannelCode())?"":tradeRequest.getChannelCode(),
                tradeRequest.getStatus()==null?"":tradeRequest.getStatus(),
                tradeRequest.getType()==null?"":tradeRequest.getType()));
        return modelAndView;
    }

    @RequestMapping(value = "/getAccountList",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public ModelAndView getAccountList(HttpServletRequest request) {
        Long groupId = Long.parseLong(request.getSession().getAttribute("groupId").toString());
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setCurrentPage(1);
        accountRequest.setPageSize(15);
        accountRequest.setGroupId(groupId);

        if(!StringUtils.isEmpty(request.getParameter("mchId"))) {
            accountRequest.setMchId(request.getParameter("mchId"));
        }

        if(!StringUtils.isEmpty(request.getParameter("groupType"))) {
            accountRequest.setGroupType(Integer.parseInt(request.getParameter("groupType")));
        }

        if(!StringUtils.isEmpty(request.getParameter("name"))) {
            accountRequest.setName(request.getParameter("name"));
        }

        Pagenation<AccountPageInfo> pagenation = client.getAccountList(accountRequest);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin_account_index");
        modelAndView.addObject("pageInfo",pagenation);
        modelAndView.addObject("requestInfo",accountRequest);

        return modelAndView;
    }

    @RequestMapping(value = "/getAdminAccountList",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public ModelAndView getAdminAccountList(HttpServletRequest request) {
        Long groupId = Long.parseLong(request.getSession().getAttribute("groupId").toString());
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setCurrentPage(1);
        accountRequest.setPageSize(15);
        accountRequest.setGroupId(groupId);

        if(!StringUtils.isEmpty(request.getParameter("mchId"))) {
            accountRequest.setMchId(request.getParameter("mchId"));
        }

        if(!StringUtils.isEmpty(request.getParameter("name"))) {
            accountRequest.setName(request.getParameter("name"));
        }

        Pagenation<AdminAccountPageInfo> pagenation = client.getAdminAccountList(accountRequest);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin_account_admin");
        modelAndView.addObject("pageInfo",pagenation);
        modelAndView.addObject("requestInfo",accountRequest);

        return modelAndView;
    }

    @RequestMapping(value = "/getOprList",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public ModelAndView getOprList(HttpServletRequest request) {
        Long groupId = Long.parseLong(request.getSession().getAttribute("groupId").toString());
        AccountOprRequest accountOprRequest = new AccountOprRequest();
        accountOprRequest.setPageSize(15);
        accountOprRequest.setCurrentPage(1);
        accountOprRequest.setGroupId(groupId);
        if(StringUtils.isNotEmpty(request.getParameter("name"))) {
            accountOprRequest.setName(request.getParameter("name"));
        }
        if(StringUtils.isNotEmpty(request.getParameter("outTradeNo"))) {
            accountOprRequest.setOutTradeNo(request.getParameter("outTradeNo"));
        }
        if(StringUtils.isNotEmpty(request.getParameter("status"))) {
            accountOprRequest.setStatus(Integer.parseInt(request.getParameter("status")));
        }
        if(StringUtils.isNotEmpty(request.getParameter("oprType"))) {
            accountOprRequest.setOprType(Integer.parseInt(request.getParameter("oprType")));
        }

        Pagenation<AccountOprInfo> pagenation = client.getAccountOprLogList(accountOprRequest);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin_order_changeRecord");
        modelAndView.addObject("pageInfo",pagenation);
        modelAndView.addObject("requestInfo",accountOprRequest);

        return modelAndView;
    }

    @RequestMapping(value = "/with_draw_finish",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    Map<String,Object> finishWithDraw(HttpServletRequest request) {
        String id = request.getParameter("id");
        if(StringUtils.isBlank(id)){
            return MapUtils.buildMap("res",false,"msg","参数错误");
        }
        boolean result = adminClient.finishWithDraw(Long.parseLong(id));
        return MapUtils.buildMap("res",result);
    }

    @RequestMapping(value = "/with_draw_failed",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    Map<String,Object> withDrawFailed(HttpServletRequest request) {
        String id = request.getParameter("id");
        if(StringUtils.isBlank(id)){
            return MapUtils.buildMap("res",false,"msg","参数错误");
        }
        boolean result = adminClient.withDrawFailed(Long.parseLong(id));
        return MapUtils.buildMap("res",result);
    }

    @RequestMapping(value = "/getWithDrawList",method = RequestMethod.GET)
    public ModelAndView getWithDrawList(HttpServletRequest request) {
        Long groupId = Long.parseLong(request.getSession().getAttribute("groupId").toString());
        WithDrawRequest withDrawRequest = new WithDrawRequest();
        withDrawRequest.setGroupId(groupId);
        withDrawRequest.setCurrentPage(1);
        withDrawRequest.setPageSize(15);
        withDrawRequest.setRole(WithDrawRole.PAYER.getValue());

        if(StringUtils.isNotEmpty(request.getParameter("status"))) {
            withDrawRequest.setStatus(Integer.parseInt(request.getParameter("status")));
        }
        if(StringUtils.isNotEmpty(request.getParameter("mchId"))) {
            withDrawRequest.setMchId(request.getParameter("mchId"));
        }

        Pagenation<WithDrawInfo> pagenation = client.getWithDrawPage(withDrawRequest);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin_withdrawal_index");
        modelAndView.addObject("pageInfo",pagenation);
        modelAndView.addObject("requestInfo",withDrawRequest);
        modelAndView.addObject("urlParams",String.format("status=%s&mchId=%s",withDrawRequest.getStatus()==null?"":withDrawRequest.getStatus(),withDrawRequest.getMchId()==null?"":withDrawRequest.getMchId()));

        return modelAndView;
    }


    @RequestMapping(value = "/save_user_provider",method = RequestMethod.POST)
    public @ResponseBody
    Map<String,Object> saveUserProvider(HttpServletRequest request) {
        String userGroupId = request.getParameter("userGroupId");
        String id = request.getParameter("id");
        String providerCode = request.getParameter("providerCode");
        String merchantNo = request.getParameter("merchantNo");
        String outletNo = request.getParameter("outletNo");
        String cipherCode = request.getParameter("cipherCode");

        boolean result = client.saveUserGroupExt(MapUtils.buildMap("groupId",userGroupId,"id",id,"providerCode",providerCode,"merchantNo",merchantNo,"outletNo",outletNo,"cipherCode",cipherCode));
        return MapUtils.buildMap("status",result);
    }

    @RequestMapping(value = "/save_sale",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    Map<String,Object> saveSale(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        String name = request.getParameter("name");
        if(StringUtils.isEmpty(name)) {
            return MapUtils.buildMap("status",false,"msg","业务员不能为空");
        }
        String loginId = request.getParameter("loginId");
        if(StringUtils.isEmpty(loginId)) {
            return MapUtils.buildMap("status",false,"msg","登录名不能为空");
        }

        String password = request.getParameter("password");
        if(StringUtils.isEmpty(password)) {
            return MapUtils.buildMap("status",false,"msg","密码不能为空");
        }

        String idCard = request.getParameter("idCard");
        if(StringUtils.isEmpty(idCard)) {
            return MapUtils.buildMap("status",false,"msg","身份证号不能为空");
        }
        String tel = request.getParameter("tel");
        if(StringUtils.isEmpty(tel)) {
            return MapUtils.buildMap("status",false,"msg","手机号不能为空");
        }
        String qq = request.getParameter("qq");
        if(StringUtils.isEmpty(qq)) {
            return MapUtils.buildMap("status",false,"msg","QQ不能为空");
        }
        String address = request.getParameter("address");
        if(StringUtils.isEmpty(address)) {
            return MapUtils.buildMap("status",false,"msg","地址不能为空");
        }
        String subGroupId = request.getParameter("subGroupId");

        ResponseResult<Boolean> responseResult = adminClient.saveSaleInfo(MapUtils.buildMap("name",name,"loginId",loginId,"idCard",idCard,"tel",tel,"qq",qq,"address",address,"subGroupId",subGroupId,"id",id));

        return MapUtils.buildMap("status",responseResult.getData(),"msg",responseResult.getMsg());
    }

    @RequestMapping(value = "/edit_sub_group",method = RequestMethod.POST ,produces = "application/json;charset=UTF-8")
    public @ResponseBody
    Map<String,Object> editSubGroup(HttpServletRequest request, HttpServletResponse response) {
        String groupId = request.getParameter("id");
        String subGroupId = request.getParameter("subGroupId");

        ResponseResult<Boolean> responseResult = adminClient.editSubGroup(MapUtils.buildMap("groupId",groupId,"subGroupId",subGroupId));
        return MapUtils.buildMap("status",responseResult.getData(),"msg",responseResult.getMsg());
    }
}
