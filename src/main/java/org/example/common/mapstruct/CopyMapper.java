package org.example.common.mapstruct;

import org.example.pojo.dto.*;
import org.example.pojo.entity.*;
import org.example.pojo.vo.*;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
		unmappedTargetPolicy = ReportingPolicy.IGNORE, // 忽略字段不匹配警告
		unmappedSourcePolicy = ReportingPolicy.IGNORE)  // 忽略源对象多余字段)
public interface CopyMapper {

	Anno AnnoDTOToAnno(AnnoDTO anno);

	AnnoVO AnnoToAnnoVO(Anno anno);

	Depart DepartDTOTodepart(DepartDTO departDTO);

	DepartVO DepartToDepartVO(Depart depart);

	ScheduleVO ScheduleToScheduleVO(Schedule schedule);

	Schedule ScheduleDTOToSchedule(ScheduleDTO scheduleDTO);

	Banner BannerDTOToBanner(BannerDTO bannerDTO);

	BannerVO BannerToBannerVO(Banner banner);

	AppointOrder AppointOrderDTOToAppoint(AppointOrderDTO appointOrderDTO);

	AppointOrderVO AppointToAppointVO(AppointOrder appointOrder);

	Doctor DoctorDTOToDoctor(DoctorDTO doctorDTO);

	DoctorVO DoctorToDoctorVO(Doctor doctor);
}
