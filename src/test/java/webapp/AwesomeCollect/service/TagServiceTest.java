package webapp.AwesomeCollect.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import webapp.AwesomeCollect.entity.Tag;
import webapp.AwesomeCollect.repository.TagRepository;

/*
    NOTE:
    1.resolveTagIdListの未登録タグを含むケースは、
      registerTag(Tag)のDB副作用（IDの自動採番）の再現が必要なため、一時保留。
      Mockito の doAnswer を用いる必要があると思われるので、その理解を深めてから実装に挑戦する！

    2.resolveTagIdをテストする場合は、別途結合テストが必要。
 */
@ExtendWith(MockitoExtension.class)
class TagServiceTest {

  @Mock
  TagRepository tagRepository;

  @InjectMocks
  TagService tagService;

  private static final int USER_ID = 1;
  private static final List<String> MOCK_LIST = List.of("Java", "Webアプリ開発", "個人開発");
  private static final List<Integer> TAG_ID_LIST = List.of(1,2,3);

  @Test
  void getTagNameListByUserId_ユーザーIDを基にタグ名リストを返す() {
    when(tagRepository.searchTagNameList(USER_ID))
        .thenReturn(MOCK_LIST);

    List<String> resultList = tagService.getTagNameListByUserId(USER_ID);

    assertEquals(MOCK_LIST, resultList, "タグ名リストが返る");
    verify(tagRepository).searchTagNameList(USER_ID);
  }

  @Test
  void prepareTagNameListByTagIdList_タグIDリストを基にタグ名リストを返す() {
    when(tagRepository.searchTagNameListByTagIdList(TAG_ID_LIST))
        .thenReturn(MOCK_LIST);

    List<String> resultList = tagService.prepareTagNameListByTagIdList(TAG_ID_LIST);

    assertIterableEquals(MOCK_LIST, resultList, "タグ名リストが返る");
    verify(tagRepository).searchTagNameListByTagIdList(TAG_ID_LIST);
  }

  @Test
  void prepareTagNameListByTagIdList_タグIDリストがnullの場合はRepositoryを呼ばずに空のリストを返す() {
    List<String> resultList = tagService.prepareTagNameListByTagIdList(null);

    assertEquals(Collections.emptyList(), resultList, "空のリストが返る");
    verifyNoInteractions(tagRepository);
  }

  @Test
  void prepareTagNameListByTagIdList_タグIDリストが空の場合はRepositoryを呼ばずに空のリストを返す() {
    List<Integer> tagIdList = Collections.emptyList();
    List<String> resultList = tagService.prepareTagNameListByTagIdList(tagIdList);

    assertEquals(Collections.emptyList(), resultList, "空のリストが返る");
    verifyNoInteractions(tagRepository);
  }

  @Test
  void prepareCombinedTagName_タグIDリストを基に取得したタグ名リストを結合した文字列を返す() {
    when(tagRepository.searchTagNameListByTagIdList(TAG_ID_LIST))
        .thenReturn(MOCK_LIST);

    String result = tagService.prepareCombinedTagName(TAG_ID_LIST);

    assertEquals(
        "Java,Webアプリ開発,個人開発",
        result,
        "カンマ区切りで結合したタグ名が返る");
  }

  @Test
  void prepareCombinedTagName_タグIDリストがnullの場合はRepositoryを呼ばずに空文字を返す() {
    String result = tagService.prepareCombinedTagName(null);

    assertEquals("", result, "空文字が返る");
    verifyNoInteractions(tagRepository);
  }

  @Test
  void prepareCombinedTagName_タグIDリストが空の場合はRepositoryを呼ばずに空文字を返す() {
    List<Integer> tagIdList = Collections.emptyList();
    String result = tagService.prepareCombinedTagName(tagIdList);

    assertEquals("", result, "空文字が返る");
    verifyNoInteractions(tagRepository);
  }

  @Test
  void resolveTagIdList_既存タグの場合はユーザーIDとタグリストを基にタグIDリストを返す() {
    when(tagRepository.searchTagIdByUserIdAndTagName(any(Tag.class)))
        .thenReturn(1,2, 3);

    List<Integer> resultList = tagService.resolveTagIdList(USER_ID, MOCK_LIST);

    assertEquals(List.of(1,2,3), resultList,"タグIDリストが返る");
  }

  @Test
  void resolveTagIdList_タグリストがnullの場合はnullを返す() {
    List<Integer> resultList = tagService.resolveTagIdList(USER_ID, null);

    assertNull(resultList);
  }

}