package com.qinshou.qinshoubox.login.contract;

import com.qinshou.networkmodule.callback.Callback;
import com.qinshou.commonmodule.base.IBaseContract;
import com.qinshou.commonmodule.base.IBaseModel;
import com.qinshou.commonmodule.base.IBaseView;
import com.qinshou.qinshoubox.login.bean.PoemBean;
import com.qinshou.qinshoubox.login.bean.UserBean;
import com.qinshou.qinshoubox.login.view.activity.SplashActivity;

/**
 * Description:{@link SplashActivity} 的契约类
 * Author: QinHao
 * Date: 2019/4/4 14:38
 */
public interface ISplashContract extends IBaseContract {
    interface ISplashModel extends IBaseModel {
        /**
         * Author: QinHao
         * Email:cqflqinhao@126.com
         * Date:2019/12/10 11:14
         * Description:获取一首随机诗词
         */
        void getRandomPoem(Callback<PoemBean> callback);
    }

    interface ISplashView extends IBaseView {

        /**
         * Author: QinHao
         * Email:cqflqinhao@126.com
         * Date:2019/12/10 11:14
         * Description:获取随机诗词成功
         *
         * @param poemBean 诗词实体类
         */
        void getRandomSuccess(PoemBean poemBean);

        /**
         * Author: QinHao
         * Email:cqflqinhao@126.com
         * Date:2019/12/10 11:14
         * Description:获取随机诗词失败
         *
         * @param e 错误信息
         */
        void getRandomFailure(Exception e);
    }

    interface ISplashPresenter {
        /**
         * Author: QinHao
         * Email:cqflqinhao@126.com
         * Date:2019/12/10 11:14
         * Description:获取随机诗词
         */
        void getRandomPoem();
    }
}
