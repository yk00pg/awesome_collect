package com.awesomecollect.provider.param;

import java.time.LocalDate;
import java.util.List;

/**
 * 期限切れのゲストユーザーを処理する際の引数を扱うレコードクラス。
 *
 * @param guestUserIdList ゲストユーザーIDリスト
 * @param expiredDate 期限切れの基準日
 */
public record ExpiredGuestUserParams(List<Integer> guestUserIdList, LocalDate expiredDate) {}
