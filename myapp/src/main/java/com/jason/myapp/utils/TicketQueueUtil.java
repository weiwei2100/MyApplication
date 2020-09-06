package com.jason.myapp.utils;

import android.content.Context;
import android.text.TextUtils;
import com.ips.concert.model.cinema.CinemaTicketInfo;
import com.meiguan.ipsplayer.base.common.db.dao.ticket.TicketPrintDao;
import com.meiguan.ipsplayer.base.common.db.model.ticket.TicketPrintModel;

import java.util.List;

public class TicketQueueUtil {

    private static TicketPrintDao tpDao;
    private static TicketQueueUtil ticketQueueUtil;

    private TicketQueueUtil() {

    }

    public static synchronized TicketQueueUtil getInstance(Context context) {

        if (ticketQueueUtil == null) {
            tpDao = new TicketPrintDao(context);
            ticketQueueUtil = new TicketQueueUtil();
        }
        return ticketQueueUtil;
    }

    /**
     * 将CinemaTicketInfo对象拆分为TicketPrintModel
     *
     * @param cinemaTicketInfo
     */
    public void push(CinemaTicketInfo cinemaTicketInfo, String ticketNumber) {
        // 判断票号
        // 拆分
        // 转换
        // 存储
        if (!tpDao.isDBTicketNo(cinemaTicketInfo.getTicketNo())) {
            for (int i = 0; i < cinemaTicketInfo.getData().getTickets().size(); i++) {
                TicketPrintModel ticketPrintModel = new TicketPrintModel();
                ticketPrintModel.setTime(System.currentTimeMillis());
                //取票号
                ticketPrintModel.setTicketNo(ticketNumber);

                ticketPrintModel.setPrintId(cinemaTicketInfo.getData().getPrintId());
                ticketPrintModel.setBookingId(cinemaTicketInfo.getData().getBookingId());
                ticketPrintModel.setConfirmationId(cinemaTicketInfo.getData().getConfirmationId());
                ticketPrintModel.setShowDateTime(cinemaTicketInfo.getData().getShowDateTime());
                ticketPrintModel.setCinemaName(cinemaTicketInfo.getData().getCinemaName());
                ticketPrintModel.setHallId(cinemaTicketInfo.getData().getHallId());
                ticketPrintModel.setHallName(cinemaTicketInfo.getData().getHallName());
                ticketPrintModel.setFilmCode(cinemaTicketInfo.getData().getFilmCode());
                ticketPrintModel.setShortName(cinemaTicketInfo.getData().getShortName());
                ticketPrintModel.setChannelCode(cinemaTicketInfo.getData().getChannelCode());
                ticketPrintModel.setChannelName(cinemaTicketInfo.getData().getChannelName());
                ticketPrintModel.setCreateDateTime(cinemaTicketInfo.getData().getCreateDateTime());
                ticketPrintModel.setOrderPrintCode(cinemaTicketInfo.getData().getOrderPrintCode());
                ticketPrintModel.setOrderPrintMsg(cinemaTicketInfo.getData().getOrderPrintMsg());
                ticketPrintModel.setTicketData(cinemaTicketInfo.getData().getTickets().get(i));
                tpDao.save(ticketPrintModel);


            }

        }
    }

    /**
     * 获取队列头部的多个TicketPrintModel对象
     *
     * @return
     */
    public List<TicketPrintModel> pop() {
        // 第一条记录
        // 判断此记录的时间戳是否过期，过期则根据ticketNo删除数据，返回null
        // 未过期，根据第一条记录的ticketNo，查询返回列表
        TicketPrintModel fristModel = tpDao.getfirstQuery();
        if (fristModel == null) {
//            Log.d("TicketQueueUtil","获取的第一个数据为null");
            return null;
        } else {
            if (!TextUtils.isEmpty(fristModel.getTicketNo())) {
//            Log.d("TicketQueueUtil","获取的第一个数据不为null");
                if (System.currentTimeMillis() - fristModel.getTime() > 60 * 60 * 1000) {
                    tpDao.deleteTicketPrintModel(fristModel.getTicketNo());
//                Log.d("TicketQueueUtil","获取第一个数据超时删除");
                    return null;
                } else {
//                Log.d("TicketQueueUtil","获取到第一个数据,获取这张票的说有数据返回");
                    return tpDao.queryTicketPrintModel(fristModel.getTicketNo());

                }
            } else {
                tpDao.deleteTicket();
                return null;
            }
        }
    }

    /**
     * 更新打印状态
     *
     * @param model
     */
    public void update(TicketPrintModel model) {
        tpDao.update(model);

    }

    /**
     * 删除某一取票号对应所有票的信息
     *
     * @param ticketNo
     */
    public void remove(String ticketNo) {
        tpDao.deleteTicketPrintModel(ticketNo);

    }

    /**
     *
     */
    public List<TicketPrintModel> Query(String ticketNo) {
        return tpDao.queryTicketPrintModel(ticketNo);

    }

}
