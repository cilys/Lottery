package com.cilys.lottery.web.conf;

/**
 * Created by admin on 2020/6/24.
 */
public interface BonusStatus {
    // 奖金状态，0该奖金已经到用户账户里，
    // 1奖金已计算但未分配给用户账户里，
    // 2奖金到账后又重新退款给系统，
    // 3默认状态，表示该方案的奖金未经过任何操作
    String BEEN_TO_USER = "0";
    String CALULATED = "1";
    String BACK = "2";
    String DEFAULT = "3";

}
