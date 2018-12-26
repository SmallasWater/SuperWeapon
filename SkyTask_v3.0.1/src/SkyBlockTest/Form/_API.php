<?php
/**
 * Created by PhpStorm.
 * User: ZXR
 * Date: 2018/12/21
 * Time: 17:20
 */

namespace SkyBlockTest\Form;


interface _API
{
    const MENU_ADMIN   = 0xe123;//设置界面
    const MENU         = 0xe124;//任务主界面
    const MENU_LIST    = 0xe125;//任务列表
    const MENU_MESSAGE = 0xe126;//任务介绍_用作点开任务
    const MENU_MY      = 0xe127;//我的任务
    const MENU_RANK    = 0xe128;//任务排行

    const ADMIN_ADD_TASK    = 0xd123;//添加任务
    const ADMIN_REMOVE_TASK = 0xd124;//删除任务
    const ADMIN_ADD         = 0xd125;//添加任务设置

    const TYPE              = "type";
    const TITLE             = "title";
    const CONTENT           = "content";
    const BUTTONS           = "buttons";
    const STEP_SLIDER       = "step_slider";
    const CUSTOM_FORM       = "custom_form";
    const FROM              = "form";
    const TEXT              = "text";

    const DEFAULT_BUTTON    = [
        self::TYPE    => self::FROM,
        self::TITLE   => "",
        self::CONTENT => "",
        self::BUTTONS => []
    ];

    const DEFAULT_INPUT     = [
        self::TYPE    => self::CUSTOM_FORM,
        self::TITLE   => "",
        self::CONTENT => [],
    ];

    const DEFAULT_STAR      = [
        "§a新手"=>"textures/ui/recipe_book_icon",
        "§b简单"=>"textures/ui/dust_selectable_1",
        "§d普通"=>"textures/ui/dust_selectable_2",
        "§6困难"=>"textures/ui/dust_selectable_3",
        "§4地狱"=>"textures/ui/ErrorGlyph"
    ];
}