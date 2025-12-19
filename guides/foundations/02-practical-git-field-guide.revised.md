# 부록 – 실전 Git – 상황별 명령과 문제해결

> 협업 플로우, 동기화/충돌, 되돌리기, 히스토리 정리, 사고 복구

이 문서는 Git을 “이론”으로 설명하지 않는다.
실전에서 터지는 상황을 기준으로, 바로 쓸 명령과 주의점만 정리한다.

---

## 0. 사고 방지 기본 규칙

주의:

* 공유 브랜치(`main`, `develop`, `release`)에서는 **히스토리 바꾸는 작업 금지**
  * `rebase`, `reset --hard`, `push --force` 하지 않는다.
* 강제 푸시는 기본값이 `--force-with-lease`다.
* 뭔가 하기 전에 최소 이 3개는 본다.

```bash
git status
git branch -vv
git log --oneline --decorate -n 20
````

---

## 1. 기본 협업 플로우 (feature → PR → merge)

### 1.1 기본 흐름

1. `main` 최신화
2. `feature/*` 브랜치 생성
3. 커밋 쌓기
4. PR 생성 → 리뷰 → merge

```bash
git switch main
git pull --ff-only

git switch -c feature/login-rate-limit

git add -A
git commit -m "feat: rate limit on login"

git push -u origin feature/login-rate-limit
```

### 1.2 merge 방식 선택 기준

* **squash merge**

  * PR 단위로 히스토리 깔끔하게 유지하고 싶을 때
* **merge commit**

  * 브랜치 단위 흐름(컨텍스트)을 보존하고 싶을 때
* **rebase merge**

  * 선형 히스토리 유지.
  * 팀이 “히스토리 변경”을 이해하고 있어야 한다.

---

## 2. 동기화: fetch/pull, divergent, rebase/merge 선택

### 2.1 fetch vs pull

* `git fetch`: 원격 상태만 가져온다. 내 작업트리는 건드리지 않는다.
* `git pull`: `fetch + merge(or rebase)`를 한 번에 한다.

  * 설정이 애매하면, pull 한 번으로 히스토리가 더러워진다.

추천 루틴:

```bash
git fetch --prune
git log --oneline --decorate --graph --all -n 30
```

### 2.2 divergent(갈라짐) 상황

문제:
로컬과 원격이 서로 다른 커밋을 들고 있다.
Git이 “merge 할지 rebase 할지” 자동 결정 못 해서 경고/에러가 난다.

해결 (내 feature 브랜치 기준):

* 팀이 rebase를 쓰면:

```bash
git switch feature/xxx
git fetch origin
git rebase origin/main
```

* 팀이 merge를 쓰면:

```bash
git switch feature/xxx
git fetch origin
git merge origin/main
```

### 2.3 pull 정책 고정(추천)

* rebase 기반:

```bash
git config --global pull.rebase true
git config --global rebase.autoStash true
```

* ff-only 기반(강하게 안전):

```bash
git config --global pull.ff only
```

---

## 3. 충돌 해결: merge/rebase conflict

### 3.1 공통 루틴

1. 충돌 파일 확인

```bash
git status
```

2. 충돌 마커 해결 (`<<<<<<<`, `=======`, `>>>>>>>`)
3. 해결한 파일 add
4. merge면 commit, rebase면 continue

### 3.2 merge 충돌

```bash
git merge origin/main

# 충돌 해결 후
git add -A
git commit
```

취소:

```bash
git merge --abort
```

### 3.3 rebase 충돌

```bash
git rebase origin/main

# 충돌 해결 후
git add -A
git rebase --continue
```

취소:

```bash
git rebase --abort
```

주의:
rebase 중에 `--skip`은 “그 커밋을 버리는 것”이다. 의도가 명확할 때만 쓴다.

---

## 4. 되돌리기: restore/reset/revert, amend

### 4.1 파일만 되돌리기

* 스테이징 취소:

```bash
git restore --staged <file>
```

* 워킹트리 변경 취소(날아감):

```bash
git restore <file>
```

### 4.2 최근 커밋 메시지 수정(푸시 전)

```bash
git commit --amend
```

### 4.3 커밋을 “없던 일로” (푸시 전)

* 커밋만 취소하고 변경사항은 남김:

```bash
git reset --soft HEAD~1
```

* 변경사항까지 날림(위험):

```bash
git reset --hard HEAD~1
```

### 4.4 이미 공유된 커밋 되돌리기(푸시 후 / 공유 브랜치)

원칙:
공유 브랜치에서는 `reset`이 아니라 `revert`.

```bash
git revert <커밋해시>
git push
```

---

## 5. 히스토리 정리: squash, rebase -i, 커밋 쪼개기

### 5.1 여러 커밋 합치기 (내 브랜치에서)

```bash
git rebase -i HEAD~5
# pick -> squash / fixup
```

### 5.2 커밋 쪼개기

```bash
git reset --soft HEAD~1
git add -p
git commit -m "part 1"

git add -p
git commit -m "part 2"
```


### 5.3 과거 커밋의 메시지/날짜 수정 (HEAD가 아닌 “어떤 커밋이든”)

> 전제: **히스토리 재작성(rewrite history)** 이다. 수정한 커밋의 해시가 바뀌고, 그 뒤의 커밋 해시도 연쇄적으로 바뀐다.
> 공유 브랜치(`main`, `develop`)에서는 금지. **내 브랜치(또는 혼자 쓰는 브랜치)**에서만 하고, 원격에 이미 푸시했다면 마지막에 `--force-with-lease`가 필요하다.

#### 5.3.1 수정 대상 커밋 찾기 / 확인

```bash
git log --oneline --decorate -n 30

# 날짜/작성자/커미터 정보를 자세히 보고 싶으면
git show --no-patch --pretty=fuller <커밋해시>
```

`pretty=fuller`에서:

* `AuthorDate` = 작성 날짜(커밋 메시지에 같이 남는 “작성 시각”)
* `CommitDate` = 커미터 날짜(히스토리 재작성 시 보통 이게 갱신됨)

#### 5.3.2 특정 커밋 하나만 수정하기 (rebase -i + edit/reword)

예: `<TARGET>` 커밋의 **메시지와 날짜를 둘 다** 바꾸고 싶다.

```bash
git rebase -i <TARGET>^
```

에디터가 열리면 `<TARGET>` 줄을 찾아:

* 메시지만 바꿀 거면 `pick` → `reword`
* 메시지/날짜(또는 내용)까지 손댈 거면 `pick` → `edit`

rebase가 해당 커밋에서 멈추면, 아래처럼 amend 한다.

##### (A) 메시지 + 날짜를 동시에 바꾸기

```bash
# 예: 2025-12-15 10:30:00 (KST)로 맞추기
export NEW_DATE="2025-12-15T10:30:00+09:00"

GIT_COMMITTER_DATE="$NEW_DATE" \
  git commit --amend -m "chore: update docs wording" --date "$NEW_DATE"
```

##### (B) 날짜만 바꾸기 (메시지 유지)

```bash
export NEW_DATE="2025-12-15T10:30:00+09:00"

GIT_COMMITTER_DATE="$NEW_DATE" \
  git commit --amend --no-edit --date "$NEW_DATE"
```

이후 계속 진행:

```bash
git rebase --continue
```

#### 5.3.3 가장 첫 커밋(루트 커밋)까지 포함해 수정하기

```bash
git rebase -i --root
```

#### 5.3.4 merge 커밋이 있는 브랜치에서(선택)

기본 rebase는 merge 커밋을 “평평하게” 만들 수 있다. merge 구조를 유지하고 싶으면:

```bash
git rebase -i --rebase-merges <TARGET>^
```

#### 5.3.5 원격에 이미 푸시한 브랜치라면 (주의)

히스토리 재작성 후에는 원격도 강제로 갱신해야 한다.

```bash
git push --force-with-lease
```

`--force-with-lease`는 “내가 마지막으로 본 원격 상태”와 다르면 실패하므로, `--force`보다 안전하다.



---

## 6. 브랜치/리모트 정리: 삭제, prune, upstream

### 6.1 로컬 브랜치 삭제

```bash
git branch -d feature/xxx
git branch -D feature/xxx
```

### 6.2 원격 브랜치 삭제

```bash
git push origin --delete feature/xxx
```

### 6.3 원격 참조 정리(prune)

```bash
git fetch --prune
# 또는
git remote prune origin
```

### 6.4 upstream(추적 브랜치) 확인

```bash
git branch -vv
```

---

## 7. 강제 푸시: 언제/어떻게

원칙:

* 강제 푸시는 “내 브랜치”에서만 한다.
* 기본은 `--force-with-lease`.

```bash
git push --force-with-lease
```

설명:

* `--force`: 남의 커밋을 덮어써도 그냥 밀어버린다.
* `--force-with-lease`: 내가 마지막으로 본 원격 상태가 아니면 실패한다.

---

## 8. stash: 잠깐 치우기 / 복원

```bash
git stash push -m "wip: before refactor"
git stash list
git stash pop
```

추적 안 된 파일까지 포함:

```bash
git stash -u
```

추천:
하루 이상 묵힐 거면 stash 말고 **WIP 브랜치 + 커밋**이 복구/리뷰가 더 쉽다.

---

## 9. 조회/분석: log/diff/blame/bisect

### 9.1 로그

```bash
git log --oneline --decorate -n 30
git log --graph --oneline --decorate --all -n 50
```

### 9.2 diff

```bash
git diff
git diff --staged
git diff main..feature/xxx
```

### 9.3 blame

```bash
git blame path/to/file
```

### 9.4 bisect (어느 커밋부터 깨졌는지)

```bash
git bisect start
git bisect bad
git bisect good <정상커밋해시>

# 테스트 후
git bisect good
# 또는
git bisect bad

git bisect reset
```

---

## 10. 태그(tag): 배포/버전

참고:
튜토리얼 단계별 “태그로 이동 + 개인 브랜치 파기”는 기존 부록 문서의 Git 파트를 그대로 쓰면 된다. (태그 체크아웃은 detached HEAD가 될 수 있음) 

### 10.1 생성/푸시

```bash
git tag v1.2.3
git push origin v1.2.3

git tag -a v1.2.3 -m "release v1.2.3"
git push origin v1.2.3
```

### 10.2 삭제

```bash
git tag -d v1.2.3
git push origin :refs/tags/v1.2.3
```

---

## 11. .gitignore / 라인엔딩(.gitattributes)

### 11.1 .gitignore 핵심

문제:
이미 추적 중인 파일은 `.gitignore`에 넣어도 계속 추적된다.

해결:

```bash
git rm -r --cached path/to/dir
git commit -m "chore: stop tracking generated files"
```

### 11.2 라인엔딩 폭발 방지(.gitattributes)

```gitattributes
* text=auto
*.sh text eol=lf
*.bat text eol=crlf
```

---

## 12. 고급/위험: submodule, 큰 파일 사고

### 12.1 submodule

```bash
git submodule add <repo-url> path/to/sub
git submodule update --init --recursive
```

주의:
submodule은 “특정 커밋 링크”다.
상위 레포에서 submodule 포인터 업데이트 커밋을 같이 올려야 한다.

### 12.2 큰 파일을 실수로 올렸다

* 단순 삭제 커밋만으로는 용량이 줄지 않는다(히스토리에 남는다).
* 히스토리에서 제거는 `git filter-repo` 같은 “히스토리 재작성” 작업이다.

  * 공유 레포에서는 팀 합의 없이 하지 않는다.

---

## 13. 자주 나오는 질문(FAQ)

### 13.1 push 했는데 “아까 커밋으로 돌아가고 싶다”

* 공유 브랜치면 `revert`.
* 내 브랜치면 `reset` + 필요하면 `--force-with-lease`.

### 13.2 강제로 뭔가 날렸을 때 살릴 수 있나?

```bash
git reflog
```

reflog에서 커밋 해시를 찾고:

```bash
git reset --hard <해시>
```

주의:
reflog는 영구 보관이 아니다. 오래 지나면 사라질 수 있다.

---

## 14. 문서 활용 요약

* 협업 흐름 / 동기화 / 충돌 / 되돌리기는 1~4장만 알아도 대부분 해결된다.
* “원인 추적”은 9장(bisect)이 제일 강력하다.
* 태그로 단계 이동하는 튜토리얼 사용 패턴은 기존 부록 문서 Git 파트 참고. 