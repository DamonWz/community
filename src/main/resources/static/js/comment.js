/**
 * 提交回复
 */
function post() {
    var id = $("#questionId").val();
    var contentValue = $("#content").val();
    comment2Target(id, 1, contentValue, "content")
}

//封装发送请求
//targetId 父级问题或者父级评论   type 1 或 2  content 评论内容  inputId 输入框id
function comment2Target(targetId, type, content, inputId) {
    if (!content) {
        alert("评论内容不能为空!");
        return;
    }
    var url = "/comment";
    $.ajax({
        url: url,
        dataType: "json",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({
            "parentId": targetId,
            "content": content,
            "type": type
        }),
        success: function (response) {
            if (response.code == 200) {
                //点击评论后直接刷新本页面
                window.location.reload();
                $("#" + inputId).val("");
            } else {
                if (response.code == 2003) {
                    var isAccepted = confirm(response.message);
                    if (isAccepted) {
                        window.open("https://github.com/login/oauth/authorize?client_id=01112c0c360a25de39fd&redirect_uri=http://localhost:8989/callback&scope=user&state=1");
                        //点击确认登录的时候，当窗口打开登录界面后，localstorage传递一个关闭窗口的请求
                        window.localStorage.setItem("closable", true);
                    }
                }
            }
        }
    })
}

//添加二级评论
function subComments(event) {
    var commentId = event.getAttribute("data-id");
    var contentValue = $("#input-" + commentId).val();
    comment2Target(commentId, 2, contentValue, "input-" + commentId);
}


/**
 * 展开二级评论
 */
function collapseComments(event) {
    var id = event.getAttribute("data-id");
    var comments = $("#comment-" + id);
    var hasClass = comments.hasClass("in");
    //console.log(hasClass);
    //console.log(comments.children().length);
    //判断当前span的class中有没有in
    if (!hasClass) {
        if (comments.children().length == 1) {
            $.getJSON("/comment/" + id, function (data) {
                $.each(data.data.reverse(), function (index, comment) {
                    var mediaLeftElement = $("<div/>", {
                        "class": "media-left"
                    });
                    var mediaBodyElement = $("<div/>", {
                        "class": "media-body"
                    }).append($("<h5/>", {
                        "class": "media-heading",
                        "html": comment.user.name
                    })).append($("<div/>", {
                        "html": comment.content
                    })).append($("<div/>", {
                        "class": "menu"
                    }).append($("<span/>", {
                        "class": "time",
                        "html": moment(comment.gmtModified).format('YYYY/MM/DD h:mm:ss a')
                    })));

                    mediaLeftElement.append($("<img/>", {
                        "class": "media-object img-rounded",
                        "src": comment.user.avatarUrl
                    }));

                    var mediaElement = $("<div/>", {
                        "class": "media"
                    }).append(mediaLeftElement).append(mediaBodyElement);

                    var commentElement = $("<div/>", {
                        "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 comments"
                    }).append(mediaElement);

                    comments.prepend(commentElement);
                });
            });
        }
        //展开二级评论
        comments.addClass("in");
        event.classList.add("active");
    } else {
        //关闭二级评论
        comments.removeClass("in");
        event.classList.remove("active");
    }
}

//展示标签选择
function showSelectTag() {
    $("#select-tag").show();
}
//关闭标签选择块
function displayTag() {
    $("#select-tag").hide();
}

//添加标签点击事件
function selectTag(e) {
    var tag = e.firstChild.nextSibling;
    var value = tag.getAttribute("data-tag");
    //previous是一个数组
    var previous = $("#tag").val();
    var tags = previous.split("，");
    if (tags.indexOf(value) == -1) {
        //当previous中不存在value时，才可以添加value标签
        if (previous) {
            //如果当前previous中已经有标签，再加入标签需要加个逗号
            $("#tag").val(previous + "，" + value);
        }
        else {
            $("#tag").val(value);
        }
    }
}






