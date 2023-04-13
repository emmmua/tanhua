package com.tanhua.dubbo.api;

import com.tanhua.model.domain.Question;

public interface QuestionApi {

    //根据用户id查询陌生人问题
    Question findByUserId(Long userId);

    //保存
    void save(Question question);

    //更新
    void update(Question question);
}
