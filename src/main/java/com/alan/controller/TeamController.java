package com.alan.controller;

import com.alan.common.BaseResponse;
import com.alan.common.ErrorCode;
import com.alan.exception.BusinessException;
import com.alan.pojo.domain.Team;
import com.alan.pojo.domain.User;
import com.alan.pojo.dto.TeamDTO;
import com.alan.pojo.vo.TeamUserVO;
import com.alan.service.TeamService;
import com.alan.service.UserService;
import com.alan.utils.AliOssUtil;
import com.alan.utils.ResultUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * @Author Alan
 * @Date 2024/5/14 22:04
 * @Description 针对表【user(用户表)】的controller
 */
@Slf4j
@RestController
@RequestMapping("/team")
public class TeamController {

    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

    @Autowired
    private AliOssUtil aliOssUtil;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    //OSS文件上传
    @PostMapping("/upload")
    public BaseResponse<String> upload(MultipartFile file) {
        log.info("OSS文件上传:{}", file);
        try {
            //获取原始文件名
            String originalFilename = file.getOriginalFilename();
            //截取原始文件名的后缀
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            //构建新文件名称
            String objectName = aliOssUtil.getFolderName() + UUID.randomUUID().toString() + extension;
            //文件请求路径
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return ResultUtils.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败:{}", e);
        }
        return ResultUtils.error("文件上传失败");
    }


    /**
     * 新增队伍
     *
     * @param teamDTO 队伍信息
     * @return 新增成功后返回队伍id
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamDTO teamDTO, HttpServletRequest request) {
        log.info("teamDTO: {}", teamDTO);
        if (teamDTO == null) {
            log.info("teamDTO is null");
            throw new BusinessException(ErrorCode.PARAM_ERROR, "队伍信息不能为空");
        }
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        BeanUtils.copyProperties(teamDTO, team);
        long id = teamService.addTeam(team, loginUser);
        return ResultUtils.success(id);
    }

    /**
     * 删除队伍
     *
     * @param id 队伍id
     * @return 删除成功后返回删除结果
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> addTeam(@RequestBody Long id) {
        log.info("id: {}", id);
        if (id == null) {
            log.info("id is null");
            throw new BusinessException(ErrorCode.PARAM_ERROR, "队伍id不能为空");
        }
        boolean remove = teamService.removeById(id);
        if (!remove) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除队伍失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 修改队伍
     *
     * @param teamDTO 队伍信息
     * @param request 请求信息
     * @return 修改成功后返回队伍id
     */
    @PostMapping("/update")
    public BaseResponse<Long> updateTeam(@RequestBody TeamDTO teamDTO, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        log.info("team: {}, loginUser: {}", teamDTO, loginUser);
        if (teamDTO == null) {
            log.info("team is null");
            throw new BusinessException(ErrorCode.PARAM_ERROR, "队伍信息不能为空");
        }
        if (loginUser == null) {
            log.info("loginUser is null");
            throw new BusinessException(ErrorCode.PARAM_ERROR, "登录用户信息不能为空");
        }
        boolean update = teamService.updateTeam(teamDTO, loginUser);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "修改队伍失败");
        }
        return ResultUtils.success(teamDTO.getId());
    }

    /**
     * 查询单个队伍信息
     *
     * @param id 队伍id
     * @return 队伍信息
     */
    @GetMapping("/query")
    public BaseResponse<Team> getTeamById(Long id) {
        log.info("id: {}", id);
        if (id <= 0) {
            log.info("id is null");
            throw new BusinessException(ErrorCode.PARAM_ERROR, "队伍id不能为空");
        }
        Team team = teamService.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询队伍失败");
        }
        return ResultUtils.success(team);
    }

    /**
     * 查询队伍列表信息
     *
     * @param teamDTO 查询队伍请求信息
     * @return 队伍列表信息
     */
    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(TeamDTO teamDTO, HttpServletRequest request) {
        log.info("teamDTO: {}", teamDTO);
        if (teamDTO == null) {
            log.info("teamDTO is null");
            throw new BusinessException(ErrorCode.PARAM_ERROR, "查询队伍请求信息不能为空");
        }
        List<TeamUserVO> list = teamService.listTeams(teamDTO, userService.isAdmin(request));
        if (list == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询队伍列表为空");
        }
        return ResultUtils.success(list);
    }

    /**
     * 分页查询队伍信息
     *
     * @param teamDTO 分页查询队伍请求信息
     * @return 队伍列表信息
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> getTeamByPage(TeamDTO teamDTO) {
        log.info("teamDTO: {}", teamDTO);
        if (teamDTO == null) {
            log.info("teamDTO is null");
            throw new BusinessException(ErrorCode.PARAM_ERROR, "查询队伍请求信息不能为空");
        }
        Team team = new Team();
        try {
            BeanUtils.copyProperties(teamDTO, team);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        Page<Team> page = new Page<>(teamDTO.getPageNum(), teamDTO.getPageSize());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> teamPage = teamService.page(page, queryWrapper);
        if (teamPage == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询队伍失败");
        }
        return ResultUtils.success(teamPage);
    }


    /**
     * 加入队伍
     *
     * @param teamDTO 加入队伍请求信息
     * @param request 请求信息
     * @return 加入成功返回true
     */
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamDTO teamDTO, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        log.info("teamDTO: {}, loginUser: {}", teamDTO, loginUser);
        if (teamDTO == null) {
            log.info("teamDTO is null");
            throw new BusinessException(ErrorCode.PARAM_ERROR, "加入队伍请求信息不能为空");
        }
        if (loginUser == null) {
            log.info("loginUser is null");
            throw new BusinessException(ErrorCode.PARAM_ERROR, "登录用户信息不能为空");
        }
        boolean join = teamService.joinTeam(teamDTO, loginUser);
        if (!join) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "加入队伍失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 退出队伍
     *
     * @param teamDTO 退出队伍请求信息
     * @param request 请求信息
     * @return 退出成功返回true
     */
    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamDTO teamDTO, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        log.info("teamDTO: {}, loginUser: {}", teamDTO, loginUser);
        if (teamDTO == null) {
            log.info("teamDTO is null");
            throw new BusinessException(ErrorCode.PARAM_ERROR, "退出队伍请求信息不能为空");
        }
        if (loginUser == null) {
            log.info("loginUser is null");
            throw new BusinessException(ErrorCode.PARAM_ERROR, "登录用户信息不能为空");
        }
        boolean quit = teamService.quitTeam(teamDTO, loginUser);
        if (!quit) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "退出队伍失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 删除队伍
     *
     * @param teamDTO 删除队伍请求信息
     * @param request 请求信息
     * @return 删除成功返回true
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody TeamDTO teamDTO, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        log.info("teamDTO: {}, loginUser: {}", teamDTO, loginUser);
        if (teamDTO == null) {
            log.info("teamDTO is null");
            throw new BusinessException(ErrorCode.PARAM_ERROR, "删除队伍请求信息不能为空");
        }
        if (loginUser == null) {
            log.info("loginUser is null");
            throw new BusinessException(ErrorCode.PARAM_ERROR, "登录用户信息不能为空");
        }
        boolean delete = teamService.deleteTeam(teamDTO.getId(), loginUser);
        if (!delete) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除队伍失败");
        }
        return ResultUtils.success(true);
    }

}